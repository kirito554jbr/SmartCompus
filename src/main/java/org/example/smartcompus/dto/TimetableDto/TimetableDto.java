package org.example.smartcompus.dto.TimetableDto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class TimetableDto {
    private Long id;
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long courseId;
    private String courseName; // To show "Maths" instead of just ID: 5
}
