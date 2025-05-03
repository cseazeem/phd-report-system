package com.manuu.phdreport.service;

import com.manuu.phdreport.database.NoticeDao;
import com.manuu.phdreport.entity.Notice;
import com.manuu.phdreport.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NoticeSevice {
    private final NoticeDao noticeDao;

    public void uploadNotice(Long uploadedById, String role, MultipartFile file, String title, String description) throws IOException {
        String uploadDir = "E:\\phdReportDocs\\New folder\\Notice";
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

    public List<Notice> getAllNotices(int page, int size) {
        int offset = page * size;
        return noticeDao.getAllNotices(offset, size);
    }

    public File getNoticeFile(Long id) {
//        Scholar scholar = scholarDao.findById(scholarId);
//        if (scholar == null) {
//            throw new UserNotFoundException("Scholar not found");
//        }
        Notice notice = noticeDao.getNoticeFile(id);
        if(Objects.isNull(notice)) {
            throw new UserNotFoundException("Report not found");
        }

        return new File(notice.getNoticePath());
    }


}
