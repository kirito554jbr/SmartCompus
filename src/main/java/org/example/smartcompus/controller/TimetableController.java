package org.example.smartcompus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartcompus.dto.TimetableDto.TimetableDto;
import org.example.smartcompus.service.interfaces.ITimetableService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
public class TimetableController {

    private final ITimetableService timetableService;

    @PostMapping
    public ResponseEntity<TimetableDto> createSchedule(@Valid @RequestBody TimetableDto dto) {
        return new ResponseEntity<>(timetableService.createSchedule(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TimetableDto>> getAllTimetables() {
        return ResponseEntity.ok(timetableService.getAllTimetables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimetableDto> getTimetableById(@PathVariable Long id) {
        return ResponseEntity.ok(timetableService.getTimetableById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<TimetableDto>> getStudentSchedule(@PathVariable Long studentId) {
        return ResponseEntity.ok(timetableService.getStudentSchedule(studentId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TimetableDto>> getTeacherSchedule(@PathVariable Long teacherId) {
        return ResponseEntity.ok(timetableService.getTeacherSchedule(teacherId));
    }

    @GetMapping("/room-availability")
    public ResponseEntity<Boolean> checkRoomAvailability(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String day,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return ResponseEntity.ok(timetableService.isRoomAvailable(roomId, date, day, startTime, endTime));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimetable(@PathVariable Long id) {
        timetableService.deleteTimetable(id);
        return ResponseEntity.noContent().build();
    }
}

