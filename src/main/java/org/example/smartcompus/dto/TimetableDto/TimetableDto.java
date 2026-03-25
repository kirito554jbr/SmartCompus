package org.example.smartcompus.dto.TimetableDto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TimetableDto {
    private Long id;
    private String day;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long courseId;
    private String courseName;
    private Long roomId;
    private String roomName;
}
