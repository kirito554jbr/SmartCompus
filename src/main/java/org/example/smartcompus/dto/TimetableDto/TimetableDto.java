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
    private String courseName;
    private Long roomId;
    private String roomName;
}
