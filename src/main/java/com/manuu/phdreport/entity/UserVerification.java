package com.manuu.phdreport.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserVerification  {
    private String email;
    private String password;
    private String role; // "PHD_SCHOLAR", "COORDINATOR", or "RAC_MEMBER"
    private String otp;
    private LocalDateTime expiresAt;
}
