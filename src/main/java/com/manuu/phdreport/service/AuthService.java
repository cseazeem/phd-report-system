package com.manuu.phdreport.service;

import com.manuu.phdreport.database.UserDaoImpl;
import com.manuu.phdreport.database.UserVerificationDaoImpl;
import com.manuu.phdreport.entity.ResendOtpRequest;
import com.manuu.phdreport.entity.User;
import com.manuu.phdreport.entity.UserRegistrationRequest;
import com.manuu.phdreport.entity.UserVerification;
import com.manuu.phdreport.exceptions.InvalidOtpException;
import com.manuu.phdreport.exceptions.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDaoImpl userDao;
    private final UserVerificationDaoImpl userVerificationDao;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserRegistrationRequest request) {
        if (userVerificationDao.findByEmail(request.getEmail()).isPresent() || Objects.nonNull(userDao.findByEmail(request.getEmail()))) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists.");
        }

        // Generate OTP
        String otp = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        // Save OTP in verification table
        userVerificationDao.saveOtp(request.getEmail(), otp, expiresAt);

        // Send OTP via email
        emailService.sendOtpEmail(request.getEmail(), otp);
    }

    public void verifyOtp(@RequestBody UserVerification request) {
        UserVerification verification = userVerificationDao.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidOtpException("No OTP found for this email."));

        if (!verification.getOtp().equals(request.getOtp()) || verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidOtpException("Invalid or expired OTP.");
        }

        // Store the actual user password (hashed)
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hashing the user password
                .role(request.getRole()) // Default role, modify based on input
                .status("PENDING")
                .build();

        userDao.createUser(newUser);

        // Delete OTP entry after successful verification
        userVerificationDao.deleteByEmail(request.getEmail());
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    public void resendOtp(ResendOtpRequest request) {
        // Check if user exists in verification table
        Optional<UserVerification> userVerificationOpt = userVerificationDao.findByEmail(request.getEmail());

        if (userVerificationOpt.isEmpty()) {
            throw new RuntimeException("User not found or not registered.");
        }

        UserVerification userVerification = userVerificationOpt.get();

        // Check if OTP is still valid
        if (userVerification.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Existing OTP is still valid. Please wait until it expires.");
        }

        // Generate new OTP
        String newOtp = generateOtp();

        // Update OTP and expiry time
        userVerification.setOtp(newOtp);
        userVerification.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        // Save updated OTP
        userVerificationDao.updateOtp(request.getEmail(), newOtp, userVerification.getExpiresAt());

        // Send OTP via email
        emailService.sendOtpEmail(request.getEmail(), newOtp);
    }


}


