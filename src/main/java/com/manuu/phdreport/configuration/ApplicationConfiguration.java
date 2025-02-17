package com.manuu.phdreport.configuration;

import com.manuu.phdreport.database.UserDaoImpl;
import com.manuu.phdreport.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.server.ResponseStatusException;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {
//    private final UserDaoImpl userRepository;

    @Bean
    public UserDetailsService userDetailsService(UserDaoImpl userDao) {
        return username -> {
            if (username == null || username.trim().isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Username must not be null or empty");
            }

            User user = userDao.findByEmail(username);
            if (user == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "User not found for email: " + username);
            }

            // Assuming `User` implements `UserDetails` or can be converted to it.
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
        };
    }
}

