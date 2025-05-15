package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.RACMember;
import com.manuu.phdreport.entity.Report;
import com.manuu.phdreport.entity.ReportDTO;
import com.manuu.phdreport.service.JwtService;
import com.manuu.phdreport.service.RACMemberService;
import com.manuu.phdreport.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
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

        Long userId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);

        if (!List.of("RAC_MEMBER").contains(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        boolean isFullyApproved = reportService.approveReport(reportId, userId, role);

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

        if (!List.of("RAC_MEMBER").contains(role)) {
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

    @GetMapping("all/reports")
    public ResponseEntity<List<ReportDTO>> getAllReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping(path = "/report/{scholarId}", produces = "application/pdf")
    public ResponseEntity<Resource> downloadReport(@PathVariable("scholarId") Long scholarId) {
        try {
            File pdfFile = reportService.getReportFile(scholarId);

            if (!pdfFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfFile.getName())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

//    @GetMapping("/assigned-to-rac")
//    public ResponseEntity<List<Report>> getAssignedReports(HttpServletRequest request) {
//        String token = request.getHeader("Authorization").replace("Bearer ", "");
//        Long userId = jwtService.extractUserId(token);  // user_id from JWT
//
//        // Get RAC Member ID (mapped via user_id)
//        Long racMemberId = jwtService.getRacMemberIdFromUserId(userId); // Implement this helper
//
//        List<Report> reports = reportService.getReportsAssignedToRacMember(racMemberId);
//        return ResponseEntity.ok(reports);
//    }

}
