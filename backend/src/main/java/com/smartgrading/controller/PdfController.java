package com.smartgrading.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smartgrading.entity.Mark;
import com.smartgrading.repository.MarkRepository;
import com.smartgrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam String type) {
        try {
            Mark.ExamType examType = Mark.ExamType.valueOf(type.toUpperCase());
            List<Mark> marksList = markRepository.findByExamType(examType);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Academic Report - " + type, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("Rank");
            table.addCell("Register No");
            table.addCell("Student Name");
            table.addCell("Marks");
            table.addCell("Status");

            // Sort marks by value descending for ranking
            marksList.sort(Comparator.comparingInt(Mark::getMarks).reversed());

            int totalMarks = 0;
            int passCount = 0;
            for (int i = 0; i < marksList.size(); i++) {
                Mark m = marksList.get(i);
                table.addCell(String.valueOf(i + 1));
                table.addCell(m.getStudent().getUsername());
                table.addCell(m.getStudent().getName());
                table.addCell(String.valueOf(m.getMarks()));
                
                int maxMark = (examType == Mark.ExamType.MODEL) ? 100 : 60;
                int passMark = maxMark / 2;
                boolean isPass = m.getMarks() >= passMark;
                table.addCell(isPass ? "PASS" : "FAIL");

                totalMarks += m.getMarks();
                if (isPass) passCount++;
            }

            document.add(table);
            document.add(new Paragraph(" "));

            if (!marksList.isEmpty()) {
                double average = (double) totalMarks / marksList.size();
                double passPercent = ((double) passCount / marksList.size()) * 100;

                document.add(new Paragraph("Class Average: " + String.format("%.2f", average)));
                document.add(new Paragraph("Pass Percentage: " + String.format("%.2f", passPercent) + "%"));
            }

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "report_" + type + ".pdf");

            return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
