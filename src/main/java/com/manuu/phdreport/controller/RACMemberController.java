package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.RACMember;
import com.manuu.phdreport.service.JwtService;
import com.manuu.phdreport.service.RACMemberService;
import com.manuu.phdreport.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rac-member")
@RequiredArgsConstructor
public class RACMemberController {
    private final ReportService reportService;
    private final JwtService jwtService;
    private final RACMemberService racMemberService;

    @PutMapping("/approve-report/{reportId}")
    public ResponseEntity<String> approveReport(
            @PathVariable Long reportId,
            @RequestHeader("Authorization") String token) {

        Long racMemberId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);

        if (!List.of("SUPERVISOR", "HOD_NOMINEE", "SUPERVISOR_NOMINEE").contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        boolean isFullyApproved = reportService.approveReport(reportId, racMemberId, role);

        if (isFullyApproved) {
            return ResponseEntity.ok("Report fully approved and signed.");
        } else {
            return ResponseEntity.ok("Report approved by " + role + ", waiting for other approvals.");
        }
    }

    @PutMapping("/reject-report/{reportId}")
    public ResponseEntity<String> rejectReport(
            @PathVariable Long reportId,
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> requestBody) {

        Long racMemberId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);

        if (!List.of("SUPERVISOR", "HOD_NOMINEE", "SUPERVISOR_NOMINEE").contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        String remarks = requestBody.get("rejectionRemarks");
        reportService.rejectReport(reportId, racMemberId, remarks, role);

        return ResponseEntity.ok("Report rejected successfully.");
    }

    // 🔹 Update an existing RAC Member
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateRACMember(@PathVariable Long id, @RequestBody RACMember racMember) {
        racMember.setId(id);
        racMemberService.updateRACMember(racMember);
        return ResponseEntity.ok("RAC Member updated successfully.");
    }

    // 🔹 Delete a RAC Member
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRACMember(@PathVariable Long id) {
        racMemberService.deleteRACMember(id);
        return ResponseEntity.ok("RAC Member deleted successfully.");
    }

    // 🔹 Get a RAC Member by ID
    @GetMapping("/{id}")
    public ResponseEntity<RACMember> getRACMemberById(@PathVariable Long id) {
        Optional<RACMember> racMember = racMemberService.getRACMemberById(id);
        return racMember.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<Page<RACMember>> getAllRACMembers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(racMemberService.getAllRACMembers(page, size));
    }



}
