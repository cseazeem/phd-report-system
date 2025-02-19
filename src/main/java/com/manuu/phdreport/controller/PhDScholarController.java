package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.Scholar;
import com.manuu.phdreport.service.ScholarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phd-scholar")
@RequiredArgsConstructor
public class PhDScholarController {

    private final ScholarService scholarService;

    @PostMapping("/submit-details")
    public ResponseEntity<String> submitScholarDetails(@RequestBody Scholar request) {
        scholarService.saveScholarDetails(request);
        return ResponseEntity.ok("PhD Scholar details submitted successfully.");
    }


}

