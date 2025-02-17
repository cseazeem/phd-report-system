package com.manuu.phdreport.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {
    private String email;
    private String password;
    private String role; // "PHD_SCHOLAR", "COORDINATOR", or "RAC_MEMBER"
}

