package org.example.smartcompus.controller;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.dto.TeacherDto.TeacherResponseDto;
import org.example.smartcompus.service.interfaces.ITeacherService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final ITeacherService teacherService;

    @GetMapping("/employee/{employeeNumber}")
    public ResponseEntity<TeacherResponseDto> getTeacherByEmployeeNumber(@PathVariable String employeeNumber) {
        return ResponseEntity.ok(teacherService.getTeacherByEmployeeNumber(employeeNumber));
    }

    @GetMapping("/speciality/{speciality}")
    public ResponseEntity<List<TeacherResponseDto>> getTeachersBySpeciality(@PathVariable String speciality) {
        return ResponseEntity.ok(teacherService.getTeachersBySpeciality(speciality));
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponseDto>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<TeacherResponseDto>> getTeachersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idUser") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        return ResponseEntity.ok(teacherService.getTeachersPaginated(page, size, sortBy, sortDirection));
    }
}

