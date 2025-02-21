package com.manuu.phdreport.entity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    private Long id;
    private Long uploadedById; // ID of the user who uploaded the notice
    private String role; // Role of the uploader (COORDINATOR, SUPERVISOR, etc.)
    private String noticePath; // Path where the PDF is stored
    private String title; // Title of the notice
    private String description; // Description of the notice
}

