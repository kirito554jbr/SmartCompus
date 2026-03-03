package org.example.smartcompus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartcompus.dto.CourseDto.CourseDto;
import org.example.smartcompus.service.interfaces.ICourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final ICourseService courseService;

    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto dto) {
        return new ResponseEntity<>(courseService.createCourse(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<CourseDto>> getCoursesByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(courseService.getCoursesTaughtByTeacher(teacherId));
    }

    @PatchMapping("/{courseId}/assign-teacher/{teacherId}")
    public ResponseEntity<CourseDto> assignTeacher(
            @PathVariable Long courseId,
            @PathVariable Long teacherId) {
        return ResponseEntity.ok(courseService.assignTeacherToCourse(courseId, teacherId));
    }

    @PatchMapping("/{courseId}/enroll-student/{studentId}")
    public ResponseEntity<String> enrollStudent(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(courseService.enrollStudentInCourse(courseId, studentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}

