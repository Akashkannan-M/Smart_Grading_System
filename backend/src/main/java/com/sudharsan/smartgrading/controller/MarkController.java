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
    public List<Mark> getAllMarks() {
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

        List<Mark> existingMarks = markRepository.findByStudentId(student.getId());
        Optional<Mark> existingMarkOpt = existingMarks.stream()
                .filter(m -> m.getSubject().getId().equals(subject.getId()))
                .findFirst();

        Mark mark;
        if (existingMarkOpt.isPresent()) {
            mark = existingMarkOpt.get();
        } else {
            mark = new Mark();
            mark.setStudent(student);
            mark.setSubject(subject);
        }
        
        mark.setCia1(markDto.getCia1() != null ? markDto.getCia1() : mark.getCia1());
        mark.setCia2(markDto.getCia2() != null ? markDto.getCia2() : mark.getCia2());
        mark.setModelExam(markDto.getModelExam() != null ? markDto.getModelExam() : mark.getModelExam());

        return markRepository.save(mark);
    }
}
