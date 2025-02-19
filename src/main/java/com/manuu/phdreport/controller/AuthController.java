package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.*;
import com.manuu.phdreport.service.AuthService;
import com.manuu.phdreport.service.AuthenticationService;
import com.manuu.phdreport.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthenticationService authenticate;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("OTP sent to email. Please verify.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody UserVerification request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok("OTP verified successfully. Admin approval required.");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestBody ResendOtpRequest request) {
        authService.resendOtp(request);
        return ResponseEntity.ok("New OTP sent to email.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        User authenticatedUser = authenticate.authenticate(authRequest);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                authenticatedUser.getEmail(),
                authenticatedUser.getPassword(),
                new ArrayList<>() // or authenticatedUser.getAuthorities() if applicable
        );
        String jwtToken = jwtService.generateToken(userDetails);
        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}


