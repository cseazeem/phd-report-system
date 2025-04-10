package com.manuu.phdreport.controller;

import com.manuu.phdreport.database.CoordinatorDao;
import com.manuu.phdreport.entity.Coordinator;
import com.manuu.phdreport.entity.RACMember;
import com.manuu.phdreport.entity.Scholar;
import com.manuu.phdreport.entity.UserResponse;
import com.manuu.phdreport.service.*;
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

@RestController
@RequestMapping("/api/coordinator")
@RequiredArgsConstructor
public class CoordinatorController {
    private final CoordinatorDao coordinatorDao;
    private final UserService userService;
    private final ScholarService scholarService;
    private final ReportService reportService;
    private final JwtService jwtUtils;
    private final RACMemberService racMemberService;

    @GetMapping("/{userId}")
    public ResponseEntity<Coordinator> getCoordinatorProfile(@PathVariable Long userId) {
        return coordinatorDao.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/scholar-users")
    public ResponseEntity<Page<Scholar>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Scholar> users = userService.getScholarUsers(page, size);
        return ResponseEntity.ok(users);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Scholar> updateScholar(
            @PathVariable Long id,
            @RequestBody Scholar updatedScholar,
            @RequestHeader("Authorization") String token) {

        // Extract coordinatorId from JWT
        String role = jwtUtils.extractRole(token);

        // Ensure only Coordinators can generate reports
        if (!"COORDINATOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Scholar scholar = scholarService.updateScholar(id, updatedScholar);
        return ResponseEntity.ok(scholar);
    }

    @PostMapping("/generate-report/{scholarId}")
    public ResponseEntity<Resource> generateReport(
            @RequestHeader("Authorization") String token,
            @PathVariable Long scholarId) {

        try {
            // Extract coordinatorId from JWT
            Long coordinatorUserId = jwtUtils.extractUserId(token);
            String role = jwtUtils.extractRole(token);

            // Ensure only Coordinators can generate reports
//            if (!"COORDINATOR".equals(role)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
//            }

            // Generate report
            String pdfPath = reportService.generateScholarReport(scholarId, coordinatorUserId);
            File pdfFile = new File(pdfPath);

            if (!pdfFile.exists()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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


    @GetMapping("/all")
    public ResponseEntity<Page<RACMember>> getAllRACMembers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(racMemberService.getAllRACMembers(page, size));
    }

    // 🔹 Update an existing RAC Member
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateRACMember(@PathVariable Long id, @RequestBody RACMember racMember) {
        racMember.setId(id);
        racMemberService.updateRACMember(racMember);
        return ResponseEntity.ok("RAC Member updated successfully.");
    }

}
