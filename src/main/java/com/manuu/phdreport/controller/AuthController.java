package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.ResendOtpRequest;
import com.manuu.phdreport.entity.UserRegistrationRequest;
import com.manuu.phdreport.entity.UserVerification;
import com.manuu.phdreport.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}


