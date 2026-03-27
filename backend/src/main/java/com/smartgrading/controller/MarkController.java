package com.smartgrading.controller;

import com.smartgrading.dto.GroupedMarkDTO;
import com.smartgrading.dto.MarkDTO;
import com.smartgrading.dto.StudentPerformanceDTO;
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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/marks")
@CrossOrigin(origins = "*")
public class MarkController {

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

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveMarks(@RequestBody List<MarkDTO> marksList) {
        if (marksList == null || marksList.isEmpty()) return ResponseEntity.badRequest().body("Empty mark list.");
        for (MarkDTO dto : marksList) {
            int limit = (dto.getExamType() == Mark.ExamType.MODEL) ? 100 : 60;
            if (dto.getMarks() < 0 || dto.getMarks() > limit) return ResponseEntity.badRequest().body("Input Error: " + dto.getExamType() + " exceeds limit " + limit);
            
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
                markRepository.saveAndFlush(mark);
            }
        }
        return ResponseEntity.ok("Marks saved successfully to database.");
    }

    @GetMapping("/grouped")
    public ResponseEntity<List<StudentPerformanceDTO>> getGroupedPerformance() {
        List<User> students = userRepository.findByRole(User.Role.STUDENT);
        List<Subject> allSubjects = subjectRepository.findAll();
        List<Mark> allMarks = markRepository.findAll();

        List<StudentPerformanceDTO> performanceList = students.stream().map(student -> {
            StudentPerformanceDTO performance = new StudentPerformanceDTO();
            performance.setStudentId(student.getId());
            performance.setName(student.getName());
            performance.setUsername(student.getUsername());
            performance.setCurrentYear(student.getCurrentYear() != null ? student.getCurrentYear() : "1st Year");

            List<GroupedMarkDTO> subjectMarksList = new ArrayList<>();
            for (String subName : SUBJECT_ORDER) {
                Subject sub = allSubjects.stream()
                        .filter(s -> s.getSubjectName().equalsIgnoreCase(subName))
                        .findFirst().orElse(null);

                if (sub != null) {
                    List<Mark> relevantMarks = allMarks.stream()
                            .filter(m -> m.getStudent().getId().equals(student.getId()) && m.getSubject().getId().equals(sub.getId()))
                            .collect(Collectors.toList());

                    Integer cia1 = relevantMarks.stream().filter(m -> m.getExamType() == Mark.ExamType.CIA1).map(Mark::getMarks).findFirst().orElse(0);
                    Integer cia2 = relevantMarks.stream().filter(m -> m.getExamType() == Mark.ExamType.CIA2).map(Mark::getMarks).findFirst().orElse(0);
                    Integer model = relevantMarks.stream().filter(m -> m.getExamType() == Mark.ExamType.MODEL).map(Mark::getMarks).findFirst().orElse(0);

                    subjectMarksList.add(new GroupedMarkDTO(subName, cia1, cia2, model));
                }
            }
            performance.setSubjectMarks(subjectMarksList);
            
            int total = subjectMarksList.stream().mapToInt(GroupedMarkDTO::getTotal).sum();
            performance.setOverallTotal(total);
            performance.setOverallAverage(total / 6.0);
            performance.setOverallPass(subjectMarksList.stream().allMatch(GroupedMarkDTO::isPass));
            return performance;
        }).collect(Collectors.toList());

        performanceList.sort((a, b) -> b.getOverallTotal() - a.getOverallTotal());
        for (int i = 0; i < performanceList.size(); i++) performanceList.get(i).setRank(i + 1);

        return ResponseEntity.ok(performanceList);
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getOrderedSubjects() {
        List<Subject> all = subjectRepository.findAll();
        List<Subject> ordered = new ArrayList<>();
        for (String name : SUBJECT_ORDER) {
            all.stream().filter(s -> s.getSubjectName().equalsIgnoreCase(name)).findFirst().ifPresent(ordered::add);
        }
        return ResponseEntity.ok(ordered);
    }
    
    @GetMapping("/students")
    public ResponseEntity<List<User>> getStudents() {
        return ResponseEntity.ok(userRepository.findByRole(User.Role.STUDENT));
    }
}
