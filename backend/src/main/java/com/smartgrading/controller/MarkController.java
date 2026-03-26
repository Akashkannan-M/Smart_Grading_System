package com.smartgrading.controller;

import com.smartgrading.dto.MarkDTO;
import com.smartgrading.entity.Mark;
import com.smartgrading.entity.Subject;
import com.smartgrading.entity.User;
import com.smartgrading.repository.MarkRepository;
import com.smartgrading.repository.SubjectRepository;
import com.smartgrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/marks")
public class MarkController {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @PostMapping
    public ResponseEntity<?> saveMarks(@RequestBody List<MarkDTO> marksList) {
        for (MarkDTO dto : marksList) {
            // Validation
            if (dto.getExamType() == Mark.ExamType.CIA1 && dto.getMarks() > 60) {
                return ResponseEntity.badRequest().body("CIA1 marks cannot exceed 60");
            }
            if (dto.getExamType() == Mark.ExamType.CIA2 && dto.getMarks() > 60) {
                return ResponseEntity.badRequest().body("CIA2 marks cannot exceed 60");
            }
            if (dto.getExamType() == Mark.ExamType.MODEL && dto.getMarks() > 100) {
                return ResponseEntity.badRequest().body("Model exam marks cannot exceed 100");
            }

            User student = userRepository.findById(dto.getStudentId()).orElse(null);
            Subject subject = subjectRepository.findById(dto.getSubjectId()).orElse(null);
            if(student != null && subject != null) {
                Mark mark = markRepository.findByStudentAndSubjectAndExamType(student, subject, dto.getExamType());
                if (mark == null) {
                    mark = new Mark();
                    mark.setStudent(student);
                    mark.setSubject(subject);
                    mark.setExamType(dto.getExamType());
                }
                mark.setMarks(dto.getMarks());
                markRepository.save(mark);
            }
        }
        return ResponseEntity.ok("Marks saved successfully");
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getMarksForStudent(@PathVariable Long studentId) {
        User student = userRepository.findById(studentId).orElse(null);
        if(student == null) return ResponseEntity.badRequest().body("Student Not Found");
        List<Mark> marks = markRepository.findByStudent(student);
        return ResponseEntity.ok(marks);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMarks() {
        return ResponseEntity.ok(markRepository.findAll());
    }

    @GetMapping("/students")
    public ResponseEntity<?> getStudents() {
        return ResponseEntity.ok(userRepository.findByRole(User.Role.STUDENT));
    }
    
    @GetMapping("/subjects")
    public ResponseEntity<?> getSubjects() {
        return ResponseEntity.ok(subjectRepository.findAll());
    }
}
