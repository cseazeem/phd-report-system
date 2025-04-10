package com.manuu.phdreport.entity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RACMember {
    private Long id;
    private Long userId; // References the user in the users table
    private String name; // Name of the RAC Member
    private String role; // SUPERVISOR, HOD_NOMINEE, SUPERVISOR_NOMINEE
    private String designation; // New field: SUPERVISOR, HOD_NOMINEE, SUPERVISOR_NOMINEE
    private String department;
    private String email;
    private String phone;
}


