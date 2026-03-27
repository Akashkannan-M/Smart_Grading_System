package com.smartgrading.controller;

import com.smartgrading.dto.DashboardSummaryDTO;
import com.smartgrading.entity.Mark;
import com.smartgrading.entity.Subject;
import com.smartgrading.entity.User;
import com.smartgrading.repository.MarkRepository;
import com.smartgrading.repository.SubjectRepository;
import com.smartgrading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired private MarkRepository markRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private UserRepository userRepository;

    private static final List<String> SUBJECT_ORDER = Arrays.asList(
        "Multimedia and Animation",
        "Object Oriented Software Engineering",
        "Storage Technology",
        "Network Security",
        "Cloud Service Management",
        "Embedded and IoT"
    );

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary(Principal principal) {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        
        List<User> students = userRepository.findByRole(User.Role.STUDENT);
        List<Subject> subjects = subjectRepository.findAll();
        List<Mark> allMarks = markRepository.findAll();

        List<DashboardSummaryDTO.SubjectRankDTO> rankings = new ArrayList<>();
        for (String subName : SUBJECT_ORDER) {
            Subject sub = subjects.stream()
                    .filter(s -> s.getSubjectName().equalsIgnoreCase(subName))
                    .findFirst().orElse(null);

            if (sub != null) {
                DashboardSummaryDTO.SubjectRankDTO subRank = new DashboardSummaryDTO.SubjectRankDTO();
                subRank.setSubjectName(subName);
                subRank.setSubjectId(sub.getId());
                
                List<DashboardSummaryDTO.StudentMarkDTO> studentMarksList = students.stream().map(std -> {
                    DashboardSummaryDTO.StudentMarkDTO mDTO = new DashboardSummaryDTO.StudentMarkDTO();
                    mDTO.setName(std.getName());
                    mDTO.setUsername(std.getUsername());
                    mDTO.setStudentId(std.getId());
                    
                    List<Mark> stdSubMarks = allMarks.stream()
                            .filter(m -> m.getStudent().getId().equals(std.getId()) && m.getSubject().getId().equals(sub.getId()))
                            .collect(Collectors.toList());
                    
                    int total = stdSubMarks.stream().mapToInt(Mark::getMarks).sum();
                    mDTO.setMarks(total);
                    boolean p1 = stdSubMarks.stream().anyMatch(m -> m.getExamType() == Mark.ExamType.CIA1 && m.getMarks() >= 30);
                    boolean p2 = stdSubMarks.stream().anyMatch(m -> m.getExamType() == Mark.ExamType.CIA2 && m.getMarks() >= 30);
                    boolean p3 = stdSubMarks.stream().anyMatch(m -> m.getExamType() == Mark.ExamType.MODEL && m.getMarks() >= 45);
                    mDTO.setPass(p1 && p2 && p3);
                    return mDTO;
                }).collect(Collectors.toList());

                studentMarksList.sort((a, b) -> b.getMarks() - a.getMarks());
                for (int i = 0; i < studentMarksList.size(); i++) studentMarksList.get(i).setRank(i + 1);
                
                subRank.setRanking(studentMarksList);
                subRank.setAverage(studentMarksList.isEmpty() ? 0 : (double)studentMarksList.stream().mapToInt(DashboardSummaryDTO.StudentMarkDTO::getMarks).sum() / studentMarksList.size());
                subRank.setPassPercentage(studentMarksList.isEmpty() ? 0 : (double)studentMarksList.stream().filter(DashboardSummaryDTO.StudentMarkDTO::isPass).count() * 100 / studentMarksList.size());
                
                rankings.add(subRank);
            }
        }
        summary.setSubjectRankings(rankings);

        DashboardSummaryDTO.OverallStatsDTO stats = new DashboardSummaryDTO.OverallStatsDTO();
        stats.setGlobalAverage(rankings.isEmpty() ? 0 : rankings.stream().mapToDouble(DashboardSummaryDTO.SubjectRankDTO::getAverage).sum() / rankings.size());
        stats.setGlobalPassRate(rankings.isEmpty() ? 0 : rankings.stream().mapToDouble(DashboardSummaryDTO.SubjectRankDTO::getPassPercentage).sum() / rankings.size());
        summary.setStatistics(stats);

        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName());
            if (user != null) {
                DashboardSummaryDTO.StudentProfileDTO profile = new DashboardSummaryDTO.StudentProfileDTO();
                profile.setName(user.getName());
                profile.setRole(user.getRole().toString());
                profile.setDepartment(user.getDepartment());
                summary.setPersonalProfile(profile);
            }
        }

        return ResponseEntity.ok(summary);
    }
}
