package com.manuu.phdreport.controller;

import com.manuu.phdreport.database.CoordinatorDao;
import com.manuu.phdreport.entity.Coordinator;
import com.manuu.phdreport.entity.Scholar;
import com.manuu.phdreport.entity.UserResponse;
import com.manuu.phdreport.service.ReportService;
import com.manuu.phdreport.service.ScholarService;
import com.manuu.phdreport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coordinator")
@RequiredArgsConstructor
public class CoordinatorController {
    private final CoordinatorDao coordinatorDao;
    private final UserService userService;
    private final ScholarService scholarService;
    private final ReportService reportService;

    @GetMapping("/{userId}")
    public ResponseEntity<Coordinator> getCoordinatorProfile(@PathVariable Long userId) {
        return coordinatorDao.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/scholar-users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "SCHOLAR") String role) {

        Page<UserResponse> users = userService.getScholarUsers(page, size, role);
        return ResponseEntity.ok(users);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Scholar> updateScholar(
            @PathVariable Long id,
            @RequestBody Scholar updatedScholar,
            @RequestHeader("Role") String role) {

        if (!"COORDINATOR".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }

        Scholar scholar = scholarService.updateScholar(id, updatedScholar);
        return ResponseEntity.ok(scholar);
    }

    @PostMapping("/generate-report/{scholarId}")
    public ResponseEntity<String> generateReport(@PathVariable Long scholarId) {
        try {
            String pdfPath = reportService.generateScholarReport(scholarId);
            return ResponseEntity.ok("Report generated: " + pdfPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating report");
        }
    }

}
