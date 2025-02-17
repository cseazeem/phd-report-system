package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.PhDScholarRequest;
import com.manuu.phdreport.service.PhDScholarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/phd-scholar")
@RequiredArgsConstructor
public class PhDScholarController {

    private final PhDScholarService scholarService;

    @PostMapping("/submit-details")
    public ResponseEntity<String> submitScholarDetails(@RequestBody PhDScholarRequest request) {
        scholarService.saveScholarDetails(request);
        return ResponseEntity.ok("PhD Scholar details submitted successfully.");
    }
}

