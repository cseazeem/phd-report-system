package com.manuu.phdreport.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhDScholarRequest {
    private Long userId;
    private String studentName;
    private String fatherName;
    private String email;
    private String batch;
    private String rollNo;
    private String passingYear;
    private String headingDate;
    private String enrollNo;  // Optional
    private String profilePhotoUrl;  // Optional

    private String supervisor;
    private String hodNominee;
    private String supervisorNominee;

    private String researchTitle;
    private String status;
    private String titleStatus;

    private String phdCoordinator;  // Optional

}

