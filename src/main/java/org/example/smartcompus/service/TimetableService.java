package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.TimetableMapper;
import org.example.smartcompus.dto.TimetableDto.TimetableDto;
import org.example.smartcompus.exceptions.ConflictException;
import org.example.smartcompus.exceptions.ResourceNotFoundException;
import org.example.smartcompus.model.Course;
import org.example.smartcompus.model.Room;
import org.example.smartcompus.model.Timetable;
import org.example.smartcompus.repository.CourseRepository;
import org.example.smartcompus.repository.RoomRepository;
import org.example.smartcompus.repository.TimetableRepository;
import org.example.smartcompus.service.interfaces.ITimetableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableService implements ITimetableService {

    private final TimetableRepository timetableRepository;
    private final CourseRepository courseRepository;
    private final RoomRepository roomRepository;
    private final TimetableMapper timetableMapper;

    @Override
    public TimetableDto createSchedule(TimetableDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (!isRoomAvailable(dto.getRoomId(), dto.getDay(), dto.getStartTime(), dto.getEndTime())) {
            throw new ConflictException("Room is already occupied at this time");
        }

        Timetable timetable = timetableMapper.toEntity(dto);
        timetable.setCourse(course);
        timetable.setRoom(room);
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
    @Transactional(readOnly = true)
    public List<TimetableDto> getTeacherSchedule(Long teacherId) {
        List<Timetable> timetables = timetableRepository.findByCourse_Teacher_IdUser(teacherId);
        return timetables.stream()
                .map(timetableMapper::toDto)
                .toList();
    }

    @Override
    public boolean isRoomAvailable(Long roomId, String day, LocalTime start, LocalTime end) {
        // Query repository to see if any timetable entry overlaps
        return !timetableRepository.existsOverlap(roomId, day, start, end);
    }

    @Override
    public TimetableDto getTimetableById(Long id) {
        return timetableRepository.findById(id)
                .map(timetableMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable not found"));
    }

    @Override
    public void deleteTimetable(Long id) {
        timetableRepository.deleteById(id);
    }
}