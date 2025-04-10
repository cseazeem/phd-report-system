package com.manuu.phdreport.controller;

import com.manuu.phdreport.entity.Signature;
import com.manuu.phdreport.service.JwtService;
import com.manuu.phdreport.service.SignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/signatures")
@RequiredArgsConstructor
public class SignatureController {

    private final SignatureService signatureService;
    private final JwtService jwtService;

    @PostMapping("/upload/{reportId}")
    public ResponseEntity<String> uploadSignature(@PathVariable Long reportId,
                                                  @RequestHeader("Authorization") String token,
                                                  @RequestParam("file") MultipartFile file,
                                                  @RequestParam("x") int x,
                                                  @RequestParam("y") int y) {
        try {
            Long racMemberId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);

            if (!List.of("RAC_MEMBER").contains(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
            }

            signatureService.uploadSignature(racMemberId, reportId, file, x, y);
            return ResponseEntity.ok("Signature uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload signature.");
        }
    }

    @GetMapping("/report/{reportId}")
    public ResponseEntity<List<Signature>> getSignaturesForReport(@PathVariable Long reportId) {
        List<Signature> signatures = signatureService.getSignaturesForReport(reportId);
        return ResponseEntity.ok(signatures);
    }

}

