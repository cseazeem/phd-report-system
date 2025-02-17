package com.manuu.phdreport.service;


import com.manuu.phdreport.database.UserDaoImpl;
import com.manuu.phdreport.entity.AuthRequest;
import com.manuu.phdreport.entity.User;
import com.manuu.phdreport.exceptions.InvalidEmailOrPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDaoImpl userDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;



    public User authenticate(AuthRequest input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new InvalidEmailOrPasswordException("Invalid email or password");
        }

        User user = userDao.findByEmail(input.getEmail());
//        if (Objects.isNull(user)){
//                throw new InvalidArgumentException("User not found");
//        }
        return user;
    }
}

