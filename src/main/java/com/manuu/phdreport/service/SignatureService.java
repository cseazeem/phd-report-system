package com.manuu.phdreport.service;

import com.manuu.phdreport.database.RACMemberDao;
import com.manuu.phdreport.database.SignatureDao;
import com.manuu.phdreport.entity.RACMember;
import com.manuu.phdreport.entity.Signature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureDao signatureDao;
    private final RACMemberDao racMemberDao;

    public void uploadSignature(Long racMemberId, Long reportId, MultipartFile file, int x, int y) throws IOException {
        String uploadDir = "C:\\Users\\Zaki Anwar\\Desktop\\Signaature\\New folder";
        RACMember racMember = racMemberDao.findById(racMemberId)
                .orElseThrow(() -> new IllegalArgumentException("RAC Member not found with ID: " + racMemberId));

        Long id = racMember.getId();
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = uploadDir + "signature_" + id + "_" + System.currentTimeMillis() + ".png";
        file.transferTo(new File(filePath));

        Signature signature = Signature.builder()
                .racMemberId(id)
                .reportId(reportId)
                .signaturePath(filePath)
                .x(x)
                .y(y)
                .build();

        signatureDao.insertSignature(signature);
    }

    public List<Signature> getSignaturesForReport(Long reportId) {
        return signatureDao.getSignaturesForReport(reportId);
    }

}

