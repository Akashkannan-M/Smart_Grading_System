package com.sudharsan.smartgrading.controller;

import com.sudharsan.smartgrading.dto.MarkDto;
import com.sudharsan.smartgrading.model.Mark;
import com.sudharsan.smartgrading.model.Subject;
import com.sudharsan.smartgrading.model.User;
import com.sudharsan.smartgrading.repository.MarkRepository;
import com.sudharsan.smartgrading.repository.SubjectRepository;
import com.sudharsan.smartgrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/marks")
public class MarkController {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public List<Mark> getAllMarks(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String examType) {
        
        System.out.println("DEBUG: subjectId=" + subjectId + ", examType=" + examType);
        
        if (subjectId != null) {
            List<Mark> filtered = markRepository.findBySubjectId(subjectId);
            System.out.println("DEBUG: Found " + filtered.size() + " records for subject");
            return filtered;
        }
        System.out.println("DEBUG: Fetching all marks (no filter)");
        return markRepository.findAll();
    }

    @GetMapping("/student/{studentId}")
    public List<Mark> getStudentMarks(@PathVariable Long studentId) {
        return markRepository.findByStudentId(studentId);
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('CC')")
    public Mark saveOrUpdateMark(@RequestBody MarkDto markDto) {
        User student = userRepository.findById(markDto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Subject subject = subjectRepository.findById(markDto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Optional<Mark> existingMarkOpt = markRepository.findByStudentIdAndSubjectId(student.getId(), subject.getId());

        Mark mark;
        if (existingMarkOpt.isPresent()) {
            mark = existingMarkOpt.get();
        } else {
            mark = new Mark();
            mark.setStudent(student);
            mark.setSubject(subject);
        }
        
        // Validation (Requirement 3: CIA1/2 max 60, Model max 100)
        validateMark(markDto.getCia1(), 60, "CIA1");
        validateMark(markDto.getCia2(), 60, "CIA2");
        validateMark(markDto.getModelExam(), 100, "Model exam");

        // Requirement 1: Store as "-" if empty
        mark.setCia1(formatMark(markDto.getCia1()));
        mark.setCia2(formatMark(markDto.getCia2()));
        mark.setModelExam(formatMark(markDto.getModelExam()));

        mark.calculateTotalAndResult();
        
        return markRepository.save(mark);
    }

    private void validateMark(String mark, int max, String label) {
        if (mark == null || mark.trim().isEmpty() || mark.equals("-") || mark.equals("AB")) return;
        try {
            int val = Integer.parseInt(mark.trim());
            if (val > max) {
                throw new RuntimeException(label + " marks cannot exceed " + max);
            }
        } catch (NumberFormatException e) {
             throw new RuntimeException("Invalid numeric value for " + label);
        }
    }

    private String formatMark(String val) {
        if (val == null || val.trim().isEmpty()) return "-"; // Requirement 1
        return val.trim().toUpperCase(); // Handle AB or numeric
    }
}
