package com.manuu.phdreport.entity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinator {
    private Long id;
    private Long userId;
    private String name;
    private String department;
    private LocalDateTime createdAt;
}

