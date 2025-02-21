package com.manuu.phdreport.entity;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signature {
    private Long id;
    private Long racMemberId;
    private Long reportId;
    private String signaturePath;  // Path to the signature image
    private int x;  // X-coordinate on PDF
    private int y;  // Y-coordinate on PDF
}

