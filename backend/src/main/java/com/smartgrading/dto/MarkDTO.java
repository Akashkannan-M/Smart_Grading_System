package com.smartgrading.dto;

import com.smartgrading.entity.Mark;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkDTO {
    private Long id;
    private Long studentId;
    private Long subjectId;
    private Mark.ExamType examType;
    private Integer marks;
}
