package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.CourseDto.CourseDto;
import org.example.smartcompus.dto.StudentDto.StudentResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICourseService {
    CourseDto createCourse(CourseDto courseDto);
    CourseDto assignTeacherToCourse(Long courseId, Long teacherId);
    List<CourseDto> getCoursesTaughtByTeacher(Long  teacherId);
    List<CourseDto> getAllCourses();
    Page<CourseDto> getCoursesPaginated(int page, int size, String sortBy, String sortDirection);
    void deleteCourse(Long id);
    CourseDto getCourseById(Long id);
    List<StudentResponseDto> getEnrolledStudents(Long courseId);
    String enrollStudentInCourse(Long courseId, Long studentId);
}