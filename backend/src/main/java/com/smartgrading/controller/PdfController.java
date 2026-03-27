package com.smartgrading.controller;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smartgrading.entity.Mark;
import com.smartgrading.entity.Subject;
import com.smartgrading.entity.User;
import com.smartgrading.repository.MarkRepository;
import com.smartgrading.repository.SubjectRepository;
import com.smartgrading.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired private MarkRepository markRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SubjectRepository subjectRepository;

    private static final List<String> SUBJECT_ORDER = Arrays.asList(
        "Multimedia and Animation",
        "Object Oriented Software Engineering",
        "Storage Technology",
        "Network Security",
        "Cloud Service Management",
        "Embedded and IoT"
    );

    @GetMapping("/download")
    public void generatePdf(HttpServletResponse response) {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=Final_Grade_Report.pdf");

            Document document = new Document(PageSize.A4.rotate()); // Landscape for wide grid
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();
            document.add(new Paragraph("Sudharsan Engineering College - Smart Grading HUB"));
            document.add(new Paragraph("Final Semester Performance Grade Sheet (CIA1 / CIA2 / MODEL)"));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(7); // Student | Sub1 | Sub2 | Sub3 | Sub4 | Sub5 | Sub6
            table.setWidthPercentage(100);

            // Headers
            table.addCell("Student Name (Reg No)");
            for (String sub : SUBJECT_ORDER) {
                table.addCell(sub.split(" ")[0]); // Short names for PDF grid
            }

            List<User> students = userRepository.findByRole(User.Role.STUDENT);
            List<Subject> subjects = subjectRepository.findAll();
            List<Mark> allMarks = markRepository.findAll();

            for (User s : students) {
                table.addCell(s.getName() + " (" + s.getUsername() + ")");
                
                for (String subName : SUBJECT_ORDER) {
                    Subject subObj = subjects.stream()
                            .filter(sub -> sub.getSubjectName().equalsIgnoreCase(subName))
                            .findFirst().orElse(null);
                    
                    if (subObj != null) {
                        List<Mark> mrks = allMarks.stream()
                                .filter(m -> m.getStudent().getId().equals(s.getId()) && m.getSubject().getId().equals(subObj.getId()))
                                .toList();
                        
                        Integer c1 = mrks.stream().filter(m -> m.getExamType() == Mark.ExamType.CIA1).map(Mark::getMarks).findFirst().orElse(0);
                        Integer c2 = mrks.stream().filter(m -> m.getExamType() == Mark.ExamType.CIA2).map(Mark::getMarks).findFirst().orElse(0);
                        Integer mod = mrks.stream().filter(m -> m.getExamType() == Mark.ExamType.MODEL).map(Mark::getMarks).findFirst().orElse(0);
                        
                        table.addCell(c1 + " | " + c2 + " | " + mod);
                    } else {
                        table.addCell("0 | 0 | 0");
                    }
                }
            }
            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
