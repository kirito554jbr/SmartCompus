package org.example.smartcompus.controller;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.dto.StudentDto.StudentResponseDto;
import org.example.smartcompus.service.interfaces.IStudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final IStudentService studentService;

    @GetMapping("/number/{studentNumber}")
    public ResponseEntity<StudentResponseDto> getStudentByNumber(@PathVariable String studentNumber) {
        return ResponseEntity.ok(studentService.getStudentByNumber(studentNumber));
    }

    @GetMapping("/major/{major}")
    public ResponseEntity<List<StudentResponseDto>> getStudentsByMajor(@PathVariable String major) {
        return ResponseEntity.ok(studentService.getStudentsByMajor(major));
    }
}

