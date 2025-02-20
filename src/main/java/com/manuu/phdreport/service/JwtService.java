package com.manuu.phdreport.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Use a constant key (should ideally come from a secure location)
    private static final String SECRET_KEY_BASE64 = "adqwuihqwuihuihiwhvfewuifhewifjewfoijfieiffiwuifdsdsdsfadqwuihqwuihuihiwhvfewuifhewifjewfoijfieiffiwuifdsdsdsf";
    private final byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY_BASE64);

    // Expiration time in milliseconds (1 hour)
    private final long jwtExpirationMs = 3600000;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Long extractUserId(String token) {
        return Long.parseLong(getClaims(token).get("id").toString());
    }

    public String extractRole(String token) {
        return getClaims(token).get("role").toString();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKeyBytes)) // Fixed
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKeyBytes))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails,String role,Long id) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", role) // Adding role for RBAC
                .claim("id", id) // Adding role for RBAC
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(secretKeyBytes), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String validateResetToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKeyBytes))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // Return the email encoded in the token
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Token has expired");
        } catch (JwtException ex) {
            throw new RuntimeException("Invalid token");
        }
    }

    public String generateResetToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes validity
                .signWith(Keys.hmacShaKeyFor(secretKeyBytes), SignatureAlgorithm.HS256) // Use your secret key
                .compact();
    }

    public long getExpirationTime() {
        return jwtExpirationMs;
    }
}
