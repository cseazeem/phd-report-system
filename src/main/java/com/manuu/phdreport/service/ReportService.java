package com.manuu.phdreport.service;

import com.manuu.phdreport.database.ScholarDao;
import com.manuu.phdreport.entity.Scholar;
import com.manuu.phdreport.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ScholarDao scholarDao;

    public String generateScholarReport(Long scholarId) throws Exception {
        Scholar scholar = scholarDao.findById(scholarId);
        if (scholar == null) {
            throw new UserNotFoundException("Scholar not found");
        }

        String docxTemplatePath = "C:\\Users\\azeem\\Desktop\\Zaki\\input\\Minutes_Template.docx";
        String generatedDocxPath = "C:\\Users\\azeem\\Desktop\\Zaki\\output\\generated_" + scholar.getRollNo() + "_" + System.currentTimeMillis() + ".docx";
        String generatedPdfPath = "C:\\Users\\azeem\\Desktop\\Zaki\\output\\generated_" + scholar.getRollNo() + "_" + System.currentTimeMillis() + ".pdf";

        replacePlaceholdersInDocx(docxTemplatePath, generatedDocxPath, scholar);

        convertDocxToPdf(generatedDocxPath, generatedPdfPath);
        System.out.println("DOCX converted to PDF successfully!");
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

}

