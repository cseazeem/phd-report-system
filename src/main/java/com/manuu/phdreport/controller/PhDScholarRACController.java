package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.AssignRequest;
import com.manuu.phdreport.entity.PhDScholarRACMember;
import com.manuu.phdreport.service.PhDScholarRACService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scholar-rac")
@RequiredArgsConstructor
public class PhDScholarRACController {

    private final PhDScholarRACService scholarRACService;

    @PostMapping("/assign")
    public ResponseEntity<String> assignRacMember(@RequestBody AssignRequest request) {
        Long id = scholarRACService.assignRacMember(request.getScholarId(), request.getRacMemberId(), request.getRole());
        return ResponseEntity.ok("Assigned RAC Member with mapping ID: " + id);
    }

    @GetMapping("/{scholarId}")
    public ResponseEntity<List<PhDScholarRACMember>> getScholarRacMembers(@PathVariable Long scholarId) {
        return ResponseEntity.ok(scholarRACService.getAssignedRacMembers(scholarId));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeRacMember(@RequestParam Long scholarId, @RequestParam Long racMemberId) {
        scholarRACService.removeRacMember(scholarId, racMemberId);
        return ResponseEntity.ok("RAC Member removed from scholar");
    }

}

