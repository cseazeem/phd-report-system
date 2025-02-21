package com.manuu.phdreport.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private Long id;
    private Long scholarId;
    private Long coordinatorId;
    private String status;  // PENDING, APPROVED, REJECTED
    private String reportPath;
}
