package com.manuu.phdreport.service;

import com.manuu.phdreport.database.NoticeDao;
import com.manuu.phdreport.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeSevice {
    private final NoticeDao noticeDao;

    public void uploadNotice(Long uploadedById, String role, MultipartFile file, String title, String description) throws IOException {
        String uploadDir = "C:\\Users\\azeem\\Desktop\\Zaki\\Notice";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = uploadDir + "notice_" + System.currentTimeMillis() + ".pdf";
        file.transferTo(new File(filePath));

        Notice notice = Notice.builder()
                .uploadedById(uploadedById)
                .role(role)
                .noticePath(filePath)
                .title(title)
                .description(description)
                .build();

        noticeDao.insertNotice(notice);
    }

    public List<Notice> getAllNotices() {
        return noticeDao.getAllNotices();
    }

}
