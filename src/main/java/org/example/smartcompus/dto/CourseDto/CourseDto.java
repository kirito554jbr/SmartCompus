package org.example.smartcompus.dto.CourseDto;

import lombok.Data;

@Data
public class CourseDto {
    private Long id;
    private String name;
    private String code;
    private Long teacherId;
    private String teacherFirstName;
    private String teacherLastName;
}
