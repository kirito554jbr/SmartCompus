package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.TimetableMapper;
import org.example.smartcompus.dto.TimetableDto.TimetableDto;
import org.example.smartcompus.model.Course;
import org.example.smartcompus.model.Timetable;
import org.example.smartcompus.repository.CourseRepository;
import org.example.smartcompus.repository.TimetableRepository;
import org.example.smartcompus.service.interfaces.ITimetableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableServiceImpl implements ITimetableService {

    private final TimetableRepository timetableRepository;
    private final CourseRepository courseRepository;
    private final TimetableMapper timetableMapper;

    @Override
    public TimetableDto createSchedule(TimetableDto dto) {
        // 1. Verify if the course exists
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // 2. Business Rule: Check if the room/teacher is available (Optional Logic)
        if (!isRoomAvailable(dto.getRoomName(), dto.getDay(), dto.getStartTime(), dto.getEndTime())) {
            throw new ConflictException("Room is already occupied at this time");
        }

        // 3. Map and Save
        Timetable timetable = timetableMapper.toEntity(dto);
        timetable.setCourse(course);
        return timetableMapper.toDto(timetableRepository.save(timetable));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableDto> getStudentSchedule(Long studentId) {
        // Logic to fetch timetable entries linked to the student's courses
        return timetableRepository.findByStudentId(studentId).stream()
                .map(timetableMapper::toDto)
                .toList();
    }

    @Override
    public boolean isRoomAvailable(String room, String day, LocalTime start, LocalTime end) {
        // Query repository to see if any timetable entry overlaps
        return !timetableRepository.existsOverlap(room, day, start, end);
    }
}