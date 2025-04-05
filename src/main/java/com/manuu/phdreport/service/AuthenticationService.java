package com.manuu.phdreport.service;


import com.manuu.phdreport.database.UserDaoImpl;
import com.manuu.phdreport.entity.AuthRequest;
import com.manuu.phdreport.entity.User;
import com.manuu.phdreport.exceptions.InvalidEmailOrPasswordException;
import com.manuu.phdreport.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        if (Objects.isNull(user)){
                throw new UserNotFoundException("User not found");
        }
        // Check user status                 CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'));
        if ("PENDING".equalsIgnoreCase(user.getStatus()) || "REJECTED".equalsIgnoreCase(user.getStatus())) {
            throw new UserNotFoundException("Your account is not approved for login");
        }
        return user;
    }
}

