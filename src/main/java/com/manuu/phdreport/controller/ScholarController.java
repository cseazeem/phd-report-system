package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.Scholar;
import com.manuu.phdreport.service.ReportService;
import com.manuu.phdreport.service.ScholarService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/api/phd-scholar")
@RequiredArgsConstructor
public class ScholarController {

    private final ScholarService scholarService;
    private final ReportService reportService;

    @PostMapping("/submit-details")
    public ResponseEntity<String> submitScholarDetails(@RequestBody Scholar request) {
        scholarService.saveScholarDetails(request);
        return ResponseEntity.ok("PhD Scholar details submitted successfully.");
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

