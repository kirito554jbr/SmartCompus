package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.CourseDto.CourseDto;

import java.util.List;

public interface ICourseService {
    CourseDto createCourse(CourseDto courseDto);
    CourseDto assignTeacherToCourse(Long courseId, Long teacherId);
    List<CourseDto> getCoursesBySpeciality(String speciality);
    List<CourseDto> getAllCourses();
    void deleteCourse(Long id);
    CourseDto getCourseById(Long id);
}