package com.manuu.phdreport.controller;

import com.manuu.phdreport.database.CoordinatorDao;
import com.manuu.phdreport.database.NoticeDao;
import com.manuu.phdreport.entity.Coordinator;
import com.manuu.phdreport.entity.Notice;
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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeSevice noticeService;
    private final JwtService jwtService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadNotice(@RequestHeader("Authorization") String token,
                                               @RequestParam("file") MultipartFile file,
                                               @RequestParam("title") String title,
                                               @RequestParam("description") String description) {
        try {
            Long uploadedById = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);

            if (!List.of("COORDINATOR", "RAC_MEMBER").contains(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
            }

            noticeService.uploadNotice(uploadedById, role, file, title, description);
            return ResponseEntity.ok("Notice uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload notice.");
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<Notice>> getAllNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Notice> notices = noticeService.getAllNotices(page, size);
        return ResponseEntity.ok(notices);
    }

    @GetMapping(path = "/{noticeId}", produces = "application/pdf")
    public ResponseEntity<Resource> downloadReport(@PathVariable("noticeId") Long noticeId) {
        try {
            File pdfFile = noticeService.getNoticeFile(noticeId);

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


}
