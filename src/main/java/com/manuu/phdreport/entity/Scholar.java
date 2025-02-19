package com.manuu.phdreport.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scholar {
    private Long id; // Unique identifier for the PhD scholar (Primary Key)
    private Long userId; // User ID in users table linked to this PhD scholar
    private String scholarName; // Name of the PhD scholar
    private String fatherName; // Father's name of the scholar
    private String email; // Scholar's registered email ID
    private String batch; // Batch of the scholar (e.g., 2020-2024)
    private String rollNo; // Roll number assigned to the scholar
    private Integer passingYear; // Year when the scholar completed the PhD
    private LocalDate headingDate; // Date of RAC (Research Advisory Committee) heading
    private LocalDate dateOfJoining; // Date of Joining
    private Integer yearOfAdmission; // Year of admission

    private String enrollmentNo;  // Enrollment number of the scholar
    private String profilePhotoUrl;  // URL of the scholar's profile photo
    private String coSupervisor;  // Name of the co-supervisor (if any)
    private String phdCoordinator;  // Name of the PhD coordinator handling the scholar
    private String nationality;  // Nationality of the scholar
    private LocalDate vivaDate;  // Date of the final viva examination (applicable only if awarded)
    private Boolean fellowship;  // Indicates if the scholar has a fellowship (true = Yes, false = No)
    private String fullTimeOrPartTime;  // Whether the scholar is full-time or part-time

    // RAC members
    private String supervisor; // Name of the primary supervisor
    private String hodNominee; // HOD nominee assigned to the scholar
    private String supervisorNominee; // Supervisor nominee assigned to the scholar

    private String researchTitle; // Title of the research being conducted
    private String status; // Status of the PhD (e.g., In Progress, Awarded, Deregistered)
    private String titleStatus; // Status of the research title (Approved, Under Review, etc.)
}
