package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.CourseMapper;
import org.example.smartcompus.dto.CourseDto.CourseDto;
import org.example.smartcompus.model.Course;
import org.example.smartcompus.model.Student;
import org.example.smartcompus.model.Teacher;
import org.example.smartcompus.repository.CourseRepository;
import org.example.smartcompus.repository.StudentRepository;
import org.example.smartcompus.repository.TeacherRepository;
import org.example.smartcompus.service.interfaces.ICourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService implements ICourseService {
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseMapper courseMapper;

    @Override
    public CourseDto createCourse(CourseDto courseDto) {
        Course course = courseMapper.toEntity(courseDto);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    @Override
    public CourseDto assignTeacherToCourse(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));
        course.setTeacher(teacher);
        return  courseMapper.toDto(courseRepository.save(course));
    }

    @Override
    public List<CourseDto> getCoursesTaughtByTeacher(Long  teacherId) {
        return courseRepository.findByTeacher_IdUser(teacherId).stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Course not found");
        }
        courseRepository.deleteById(id);
    }

    @Override
    public CourseDto getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(courseMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Override
    public String enrollStudentInCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        if (student.getCourses().contains(course)) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        student.getCourses().add(course);
        studentRepository.save(student);
        return "Student enrolled successfully (studentId=" + studentId + ", courseId=" + courseId + ")";
    }
}
