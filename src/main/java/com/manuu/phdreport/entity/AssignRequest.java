package com.manuu.phdreport.entity;

import lombok.Data;

@Data
public class AssignRequest {
    private Long scholarId;
    private Long racMemberId;
    private String role;  // SUPERVISOR, HOD_NOMINEE, etc.
}
