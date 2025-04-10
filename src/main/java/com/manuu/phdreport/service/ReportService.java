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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ScholarDao scholarDao;
    private final ReportDaoImpl reportDao;
    private final CoordinatorDao coordinatorDao;
    private final ReportApprovalDao reportApprovalDao;
    private final SignatureDao signatureDao;
    private final RACMemberDao racMemberDao;
    // Path to LibreOffice executable
    private static final String LIBRE_OFFICE_EXE =
            "C:\\Program Files\\LibreOffice\\program\\soffice.exe";

    public String generateScholarReport(Long scholarId, Long coordinatorUserId) throws Exception {
        Scholar scholar = scholarDao.findById(scholarId);
        if (scholar == null) {
            throw new UserNotFoundException("Scholar not found");
        }

        // ✅ Check if report already exists
        Report existingReport = reportDao.findByScholarId(scholarId);
//        if (existingReport != null) {
//            throw new Exception("Report already generated for this scholar.");
//        }


//E:\phdReportDocs
        String docxTemplatePath = "C:\\Users\\Zaki Anwar\\Desktop\\New folder (2)\\Minutes_Template.docx";
        String generatedDocxPath = "E:\\phdReportDocs\\New foldergenerated_" + scholar.getRollNo() + "_" + System.currentTimeMillis() + ".docx";
        String generatedPdfPath = "E:\\phdReportDocs\\New foldergenerated_" + scholar.getRollNo() + "_" + System.currentTimeMillis() + ".pdf";

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

//    private void replacePlaceholdersInDocx(String templatePath, String outputPath, Scholar scholar) throws Exception {
//        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(templatePath)))) {
//
//            for (XWPFParagraph p : doc.getParagraphs()) {
//                for (XWPFRun r : p.getRuns()) {
//                    String text = r.getText(0);
//                    if (text != null) {
//                        text = text.replace("{{studentName}}", scholar.getScholarName())
//                                .replace("{{batch}}", scholar.getBatch())
//                                .replace("{{rollNo}}", scholar.getRollNo())
//                                .replace("{{headingDate}}", scholar.getHeadingDate().toString())
//                                .replace("{{supervisor}}", scholar.getSupervisor())
//                                .replace("{{hodNominee}}", scholar.getHodNominee())
//                                .replace("{{supervisorNominee}}", scholar.getSupervisorNominee())
//                                .replace("{{researchTitle}}", scholar.getResearchTitle())
//                                .replace("{{titleStatus}}", scholar.getTitleStatus());
//                        r.setText(text, 0);
//                    }
//                }
//            }
//
//            // Attempt writing with retries if file is locked
//            boolean fileWritten = false;
//            int retries = 3;
//            while (!fileWritten && retries > 0) {
//                try (FileOutputStream out = new FileOutputStream(outputPath)) {
//                    doc.write(out);
//                    fileWritten = true;
//                } catch (IOException e) {
//                    if (e.getMessage().contains("being used by another process")) {
//                        System.err.println("File locked, retrying in 500ms...");
//                        Thread.sleep(500);
//                        retries--;
//                    } else {
//                        throw e;
//                    }
//                }
//            }
//
//            if (!fileWritten) {
//                throw new IOException("Failed to write file after multiple attempts.");
//            }
//        }
//    }


    private void replacePlaceholdersInDocx(String templatePath, String outputPath, Scholar scholar) throws Exception {
        File templateFile = new File(templatePath);
        File outputFile = new File(outputPath);
        File tempDir = new File("temp_docx_" + System.currentTimeMillis()); // Unique temp directory

        try {
            // Step 1: Extract the .docx ZIP contents
            tempDir.mkdir();
            unzip(templateFile, tempDir);

            // Step 2: Locate and modify document.xml
            File documentXml = new File(tempDir, "word/document.xml");
            if (documentXml.exists()) {
                String xmlContent = readFile(documentXml);

                // Step 3: Replace all placeholders with Scholar data
                String modifiedContent = xmlContent
                        .replace("{{studentName}}", scholar.getScholarName())
                        .replace("{{batch}}", scholar.getBatch())
                        .replace("{{rollNo}}", scholar.getRollNo())
                        .replace("{{headingDate}}", scholar.getHeadingDate().toString())
                        .replace("{{supervisor}}", scholar.getSupervisor())
                        .replace("{{hodNominee}}", scholar.getHodNominee())
                        .replace("{{supervisorNominee}}", scholar.getSupervisorNominee())
                        .replace("{{researchTitle}}", scholar.getResearchTitle())
                        .replace("{{titleStatus}}", scholar.getTitleStatus());

                // Step 4: Write back the modified content
                writeFile(documentXml, modifiedContent);
            } else {
                throw new IOException("document.xml not found in the .docx file.");
            }

            // Step 5: Repackage into a new .docx file with retries
            boolean fileWritten = false;
            int retries = 3;
            while (!fileWritten && retries > 0) {
                try {
                    zipFolder(tempDir, outputFile);
                    fileWritten = true;
                } catch (IOException e) {
                    if (e.getMessage() != null && e.getMessage().contains("being used by another process")) {
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

        } finally {
            // Clean up temporary directory
            deleteDirectory(tempDir);
        }
    }

    // Unzip the .docx file
    private void unzip(File zipFile, File destDir) throws IOException {
        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    newFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    // Zip the folder back into a .docx file
    private void zipFolder(File folder, File zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            addFolderToZip("", folder, zos);
        }
    }

    private void addFolderToZip(String path, File folder, ZipOutputStream zos) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addFolderToZip(path + file.getName() + "/", file, zos);
            } else {
                byte[] buffer = new byte[1024];
                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(path + file.getName()));
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    // Read file content as string
    private String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    // Write string to file
    private void writeFile(File file, String content) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bw.write(content);
        }
    }

    // Delete temporary directory
    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }

//    private void convertDocxToPdf(String docxPath, String pdfPath) throws Exception {
//        try (XWPFDocument document = new XWPFDocument(new FileInputStream(docxPath));
//             PdfWriter writer = new PdfWriter(pdfPath);
//             PdfDocument pdfDoc = new PdfDocument(writer);
//             Document pdfDocument = new Document(pdfDoc)) {
//
//            // Extract text from DOCX
//            for (XWPFParagraph para : document.getParagraphs()) {
//                pdfDocument.add(new Paragraph(para.getText()));
//            }
//
//            System.out.println("PDF generated successfully at: " + pdfPath);
//        } catch (Exception e) {
//            throw new IOException("Error converting DOCX to PDF: " + e.getMessage(), e);
//        }
//    }

    public void convertDocxToPdf(String docxPath, String pdfPath)
            throws IOException, InterruptedException {

        File docxFile = new File(docxPath);
        File pdfFile = new File(pdfPath);

        if (!docxFile.exists()) {
            throw new FileNotFoundException("DOCX file not found: " + docxPath);
        }

        // Create parent directory for PDF if it doesn't exist
        pdfFile.getParentFile().mkdirs();

        ProcessBuilder pb = new ProcessBuilder(
                LIBRE_OFFICE_EXE,
                "--headless",
                "--convert-to", "pdf",
                "--outdir", pdfFile.getParent(),
                docxFile.getAbsolutePath()
        );

        // Redirect error stream to see any conversion errors
        pb.redirectErrorStream(true);

        Process process = pb.start();

        // Read output to prevent process hanging
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[LibreOffice] " + line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("Conversion failed with exit code: " + exitCode);
        }

        // LibreOffice names output file with the same name but .pdf extension
        String convertedName = docxFile.getName().replaceFirst("[.][^.]+$", "") + ".pdf";
        File convertedFile = new File(pdfFile.getParent(), convertedName);

        if (!convertedFile.exists()) {
            throw new IOException("Converted PDF not found at: " + convertedFile);
        }

        // Rename to the requested filename if different
        if (!convertedFile.getAbsolutePath().equals(pdfFile.getAbsolutePath())) {
            if (!convertedFile.renameTo(pdfFile)) {
                throw new IOException("Failed to rename output file to: " + pdfFile);
            }
        }

        System.out.println("Successfully converted to: " + pdfFile.getAbsolutePath());
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
        // Fetch the report
        Report report = Optional.ofNullable(reportDao.findById(reportId))
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));

        // Get associated signatures
        List<Signature> signatures = signatureDao.getSignaturesForReport(reportId);

        // Paths
        String pdfPath = report.getReportPath();
        String signedPdfPath = pdfPath.replace(".pdf", "_signed.pdf");

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfPath), new PdfWriter(signedPdfPath))) {
            Document doc = new Document(pdfDoc);

            // A4 fixed positions (only if DB doesn’t have them)
            float[] defaultX = {100, 250, 400};
            float defaultY = 120;

            for (int i = 0; i < signatures.size(); i++) {
                Signature signature = signatures.get(i);

                // Load image
                Image img = new Image(ImageDataFactory.create(signature.getSignaturePath()));

                // Optional: Resize image to a consistent scale
                img.scaleToFit(100, 40);

                // If DB coordinates are 0 or invalid, fallback to fixed
                float x = signature.getX() <= 0 ? defaultX[i] : signature.getX();
                float y = signature.getY() <= 0 ? defaultY : signature.getY();

                // Set position on page 1 (or you can use `pdfDoc.getNumberOfPages()` to target last page)
                img.setFixedPosition(pdfDoc.getNumberOfPages(), x, y);

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

    public File getSignatureFileByUserId(Long userId) {
        Long racId = racMemberDao.findId(userId);
        Signature signature = signatureDao.findById(racId);

        if (signature == null) {
            throw new UserNotFoundException("Signature not found");
        }

        return new File(signature.getSignaturePath());
    }


}

