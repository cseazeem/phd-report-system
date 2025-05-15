package com.manuu.phdreport.entity;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhDScholarRACMember {
    private Long id;
    private Long phdScholarId;
    private Long racMemberId;
    private String role;
}

