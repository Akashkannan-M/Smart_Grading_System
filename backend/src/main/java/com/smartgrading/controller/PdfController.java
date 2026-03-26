package com.smartgrading.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smartgrading.entity.Mark;
import com.smartgrading.entity.User;
import com.smartgrading.repository.MarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
public class PdfController {

    @Autowired
    private MarkRepository markRepository;

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam(required = false) String type) {
        try {
            java.util.List<Mark> allMarks = markRepository.findAll();
            
            // Map to hold Student -> Performance Map
            Map<User, java.util.List<Mark>> studentMarks = allMarks.stream()
                    .collect(Collectors.groupingBy(Mark::getStudent));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);

            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Smart Grading System - Final Grade Sheet", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.addCell("Register Name");
            table.addCell("CIA 1 (60)");
            table.addCell("CIA 2 (60)");
            table.addCell("MODEL (100)");
            table.addCell("TOTAL (220)");
            table.addCell("AVG %");
            table.addCell("RANK");

            java.util.List<StudentSummary> summaries = new ArrayList<>();
            for (User student : studentMarks.keySet()) {
                java.util.List<Mark> marks = studentMarks.get(student);
                int c1 = getMarkValue(marks, Mark.ExamType.CIA1);
                int c2 = getMarkValue(marks, Mark.ExamType.CIA2);
                int md = getMarkValue(marks, Mark.ExamType.MODEL);
                int total = c1 + c2 + md;
                double avg = (total / 220.0) * 100.0;
                summaries.add(new StudentSummary(student.getName(), c1, c2, md, total, avg));
            }

            summaries.sort(Comparator.comparingInt(StudentSummary::getTotal).reversed());

            int passedStudents = 0;
            double totalClassAvg = 0;

            for (int i = 0; i < summaries.size(); i++) {
                StudentSummary s = summaries.get(i);
                table.addCell(s.name);
                table.addCell(String.valueOf(s.c1));
                table.addCell(String.valueOf(s.c2));
                table.addCell(String.valueOf(s.md));
                table.addCell(String.valueOf(s.total));
                table.addCell(String.format("%.1f", s.avg) + "%");
                table.addCell(String.valueOf(i + 1));

                // PASS RULES: 30, 30, 45
                if (s.c1 >= 30 && s.c2 >= 30 && s.md >= 45) passedStudents++;
                totalClassAvg += s.avg;
            }

            document.add(table);
            document.add(new Paragraph(" "));

            if (!summaries.isEmpty()) {
                double avgClass = totalClassAvg / summaries.size();
                double passRate = (passedStudents * 100.0) / summaries.size();
                document.add(new Paragraph("FINAL ANALYTICS:"));
                document.add(new Paragraph("Total Student Count: " + summaries.size()));
                document.add(new Paragraph("Overall Class Average: " + String.format("%.2f", avgClass) + "%"));
                document.add(new Paragraph("Class Pass Percentage: " + String.format("%.2f", passRate) + "%"));
            }

            document.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "final_report.pdf");
            return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private int getMarkValue(java.util.List<Mark> marks, Mark.ExamType type) {
        return marks.stream().filter(m -> m.getExamType() == type).map(Mark::getMarks).findFirst().orElse(0);
    }

    private static class StudentSummary {
        String name;
        int c1, c2, md, total;
        double avg;
        StudentSummary(String n, int c1, int c2, int md, int t, double a) {
            this.name = n; this.c1 = c1; this.c2 = c2; this.md = md; this.total = t; this.avg = a;
        }
        int getTotal() { return total; }
    }
}
