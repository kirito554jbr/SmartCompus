package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.TimetableDto.TimetableDto;

import java.time.LocalTime;
import java.util.List;

public interface ITimetableService {
    TimetableDto createSchedule(TimetableDto dto);
    List<TimetableDto> getStudentSchedule(Long studentId);
    List<TimetableDto> getTeacherSchedule(Long teacherId);
    boolean isRoomAvailable(String roomName, String day, LocalTime startTime, LocalTime endTime);
    TimetableDto getTimetableById(Long id);
    void deleteTimetable(Long id);
}
