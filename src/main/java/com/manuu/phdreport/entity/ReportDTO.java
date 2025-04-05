package com.manuu.phdreport.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private Long scholarId;
    private Long coordinatorId;
    private String reportStatus;  // Updated to match alias in query
    private String reportPath;

    // Extra fields from phd_scholars table
    private String scholarName;
    private String email;
    private String batch;
    private String rollNo;
    private String supervisor;
    private String researchTitle;
    private String scholarStatus;  // Updated to match alias in query
}
