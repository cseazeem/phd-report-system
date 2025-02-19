package com.manuu.phdreport.entity;

import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private String role;
    private String status; // PENDING, APPROVED, REJECTED
}

