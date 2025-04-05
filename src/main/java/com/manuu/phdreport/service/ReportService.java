package com.manuu.phdreport.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.manuu.phdreport.database.*;
import com.manuu.phdreport.entity.*;
import com.manuu.phdreport.exceptions.ReportNotFoundException;
import com.manuu.phdreport.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ScholarDao scholarDao;
    private final ReportDaoImpl reportDao;
    private final CoordinatorDao coordinatorDao;
    private final ReportApprovalDao reportApprovalDao;
    private final SignatureDao signatureDao;
    private final RACMemberDao racMemberDao;

    public String generateScholarReport(Long scholarId, Long coordinatorUserId) throws Exception {
        Scholar scholar = scholarDao.findById(scholarId);
        if (scholar == null) {
            throw new UserNotFoundException("Scholar not found");
        }

        // ✅ Check if report already exists
        Report existingReport = reportDao.findByScholarId(scholarId);
        if (existingReport != null) {
            throw new Exception("Report already generated for this scholar.");
        }

        String docxTemplatePath = "C:\\Users\\Zaki Anwar\\Desktop\\New folder (2)\\Minutes_Template.docx";
        String generatedDocxPath = "C:\\Users\\Zaki Anwar\\Desktop\\New folder (2)generated_" + scholar.getRollNo() + "_" + System.currentTimeMillis() + ".docx";
        String generatedPdfPath = "C:\\Users\\Zaki Anwar\\Desktop\\New folder (2)generated_" + scholar.getRollNo() + "_" + System.currentTimeMillis() + ".pdf";

        replacePlaceholdersInDocx(docxTemplatePath, generatedDocxPath, scholar);

        convertDocxToPdf(generatedDocxPath, generatedPdfPath);
        System.out.println("DOCX converted to PDF successfully!");

        Long coordinatorId = coordinatorDao.findById(coordinatorUserId)
                .map(coordinator -> coordinator.getId())
                .orElseThrow(() -> new UserNotFoundException("Coordinator not found"));

        // Save report details in the database using JDBI
        Report report = new Report();
        report.setScholarId(scholarId);
        report.setCoordinatorId(coordinatorId);
        report.setStatus("PENDING");
        report.setReportPath(generatedPdfPath);

        reportDao.insertReport(report); // Using JDBI to insert report


        return generatedPdfPath;
    }

    private void replacePlaceholdersInDocx(String templatePath, String outputPath, Scholar scholar) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(templatePath)))) {

            for (XWPFParagraph p : doc.getParagraphs()) {
                for (XWPFRun r : p.getRuns()) {
                    String text = r.getText(0);
                    if (text != null) {
                        text = text.replace("{{studentName}}", scholar.getScholarName())
                                .replace("{{batch}}", scholar.getBatch())
                                .replace("{{rollNo}}", scholar.getRollNo())
                                .replace("{{headingDate}}", scholar.getHeadingDate().toString())
                                .replace("{{supervisor}}", scholar.getSupervisor())
                                .replace("{{hodNominee}}", scholar.getHodNominee())
                                .replace("{{supervisorNominee}}", scholar.getSupervisorNominee())
                                .replace("{{researchTitle}}", scholar.getResearchTitle())
                                .replace("{{titleStatus}}", scholar.getTitleStatus());
                        r.setText(text, 0);
                    }
                }
            }

            // Attempt writing with retries if file is locked
            boolean fileWritten = false;
            int retries = 3;
            while (!fileWritten && retries > 0) {
                try (FileOutputStream out = new FileOutputStream(outputPath)) {
                    doc.write(out);
                    fileWritten = true;
                } catch (IOException e) {
                    if (e.getMessage().contains("being used by another process")) {
                        System.err.println("File locked, retrying in 500ms...");
                        Thread.sleep(500);
                        retries--;
                    } else {
                        throw e;
                    }
                }
            }

            if (!fileWritten) {
                throw new IOException("Failed to write file after multiple attempts.");
            }
        }
    }

    private void convertDocxToPdf(String docxPath, String pdfPath) throws Exception {
        try (XWPFDocument document = new XWPFDocument(new FileInputStream(docxPath));
             PdfWriter writer = new PdfWriter(pdfPath);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document pdfDocument = new Document(pdfDoc)) {

            // Extract text from DOCX
            for (XWPFParagraph para : document.getParagraphs()) {
                pdfDocument.add(new Paragraph(para.getText()));
            }

            System.out.println("PDF generated successfully at: " + pdfPath);
        } catch (Exception e) {
            throw new IOException("Error converting DOCX to PDF: " + e.getMessage(), e);
        }
    }

    public File getReportFile(Long scholarId) {
        Scholar scholar = scholarDao.findById(scholarId);
        if (scholar == null) {
            throw new UserNotFoundException("Scholar not found");
        }
        Report report = reportDao.findByScholarId(scholarId);
        if(Objects.isNull(report)) {
            throw new UserNotFoundException("Report not found");
        }

        return new File(report.getReportPath());
    }


    public boolean approveReport(Long reportId, Long userId, String role) {
        Long racMemberId = racMemberDao.findId(userId);
        Report report = reportDao.findById(reportId);
        if (report == null) {
            throw new ReportNotFoundException("Report not found");
        }

        // Save RAC Member approval
        reportApprovalDao.saveApproval(reportId, racMemberId, role, "APPROVED","");

        // Check if all 3 RAC Members have approved
        boolean isFullyApproved = reportApprovalDao.isReportFullyApproved(reportId);

        if (isFullyApproved) {
            // Add digital signatures to the report
            addSignaturesToReport(reportId);
            reportDao.updateReportStatus(reportId, "APPROVED");
        }
        return isFullyApproved;
    }

    public void rejectReport(Long reportId, Long racMemberId, String remarks, String role) {
        Report report = Optional.ofNullable(reportDao.findById(reportId))
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));

        // Save rejection
        reportApprovalDao.saveApproval(reportId, racMemberId, role,"REJECTED", remarks);

        // Update report status to REJECTED
        reportDao.updateReportStatus(reportId, "REJECTED");
    }

    public void addSignaturesToReport(Long reportId) {
        Report report = Optional.ofNullable(reportDao.findById(reportId))
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));

        List<Signature> signatures = signatureDao.getSignaturesForReport(reportId);

        String pdfPath = report.getReportPath();
        String signedPdfPath = pdfPath.replace(".pdf", "_signed.pdf");

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfPath), new PdfWriter(signedPdfPath))) {
            Document doc = new Document(pdfDoc);

            for (Signature signature : signatures) {
                Image img = new Image(ImageDataFactory.create(signature.getSignaturePath()));
                img.setFixedPosition(signature.getX(), signature.getY());
                doc.add(img);
            }

            doc.close();
            reportDao.updateReportPath(reportId, signedPdfPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to add signatures to PDF", e);
        }
    }

    // 🔹 Get all RAC Members
    public List<ReportDTO> getAllReports() {
//        int offset = (page - 1) * size; // Calculate offset for pagination
        List<ReportDTO> reports = reportDao.findAllReports();
//        int totalRecords = racMemberDao.getTotalRACMembers();

        return reports;
//        return new PageImpl<>(racMembers, PageRequest.of(page, size), totalRecords);
    }

}

