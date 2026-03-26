package com.smartgrading.controller;

import com.smartgrading.dto.MarkDTO;
import com.smartgrading.dto.MarkResponseDTO;
import com.smartgrading.entity.Mark;
import com.smartgrading.entity.Subject;
import com.smartgrading.entity.User;
import com.smartgrading.repository.MarkRepository;
import com.smartgrading.repository.SubjectRepository;
import com.smartgrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/marks")
@CrossOrigin(origins = "*") // Explicitly allowing CORS
public class MarkController {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveMarks(@RequestBody List<MarkDTO> marksList) {
        for (MarkDTO dto : marksList) {
            // Strict Validation
            int maxMark = (dto.getExamType() == Mark.ExamType.MODEL) ? 100 : 60;
            if (dto.getMarks() > maxMark) {
                return ResponseEntity.badRequest().body("Marks overflow for " + dto.getExamType());
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
        return ResponseEntity.ok("Successfully updated student performance.");
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<MarkResponseDTO>> getMarksForStudent(@PathVariable Long studentId) {
        User student = userRepository.findById(studentId).orElse(null);
        if(student == null) return ResponseEntity.badRequest().build();
        
        List<Mark> marks = markRepository.findByStudent(student);
        return ResponseEntity.ok(convertToDTOList(marks));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MarkResponseDTO>> getAllMarks() {
        List<Mark> marks = markRepository.findAll();
        return ResponseEntity.ok(convertToDTOList(marks));
    }

    @GetMapping("/students")
    public ResponseEntity<List<User>> getStudents() {
        return ResponseEntity.ok(userRepository.findByRole(User.Role.STUDENT));
    }
    
    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getSubjects() {
        return ResponseEntity.ok(subjectRepository.findAll());
    }

    private List<MarkResponseDTO> convertToDTOList(List<Mark> marks) {
        return marks.stream().map(m -> {
            int maxMarks = (m.getExamType() == Mark.ExamType.MODEL) ? 100 : 60;
            int passMark = (m.getExamType() == Mark.ExamType.MODEL) ? 45 : 30; // Rule from prompt
            boolean isPass = m.getMarks() >= passMark;
            
            return new MarkResponseDTO(
                m.getId(),
                m.getStudent().getId(),
                m.getStudent().getName(),
                m.getSubject().getId(),
                m.getSubject().getSubjectName(),
                m.getExamType().toString(),
                m.getMarks(),
                maxMarks,
                isPass
            );
        }).collect(Collectors.toList());
    }
}
