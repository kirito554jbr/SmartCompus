package org.example.smartcompus.service;

import org.example.smartcompus.Mappers.CourseMapper;
import org.example.smartcompus.dto.CourseDto.CourseDto;
import org.example.smartcompus.model.Course;
import org.example.smartcompus.model.Student;
import org.example.smartcompus.model.Teacher;
import org.example.smartcompus.model.enums.UserRole;
import org.example.smartcompus.repository.CourseRepository;
import org.example.smartcompus.repository.StudentRepository;
import org.example.smartcompus.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock private CourseRepository courseRepository;
    @Mock private TeacherRepository teacherRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CourseMapper courseMapper;

    @InjectMocks
    private CourseService courseService;

    private Teacher teacher;
    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setIdUser(1L);
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");
        teacher.setRole(UserRole.ROLE_TEACHER);
        teacher.setEmployeeNumber("EMP001");
        teacher.setSpeciality("Mathematics");

        student = new Student();
        student.setIdUser(2L);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setRole(UserRole.ROLE_STUDENT);
        student.setStudentNumber("STU001");
        student.setMajor("Computer Science");
        student.setCourses(new ArrayList<>());

        course = new Course();
        course.setIdCourse(10L);
        course.setName("Calculus");
        course.setCode("MATH101");
    }

    // ===================== Create Course =====================

    @Nested
    @DisplayName("Create Course Tests")
    class CreateCourseTests {

        @Test
        @DisplayName("Create course - should save and return CourseDto")
        void createCourse_ShouldReturnCourseDto() {
            // Arrange
            CourseDto inputDto = new CourseDto();
            inputDto.setName("Calculus");
            inputDto.setCode("MATH101");

            Course mappedCourse = new Course();
            mappedCourse.setName("Calculus");
            mappedCourse.setCode("MATH101");

            Course savedCourse = new Course();
            savedCourse.setIdCourse(10L);
            savedCourse.setName("Calculus");
            savedCourse.setCode("MATH101");

            CourseDto outputDto = new CourseDto();
            outputDto.setId(10L);
            outputDto.setName("Calculus");
            outputDto.setCode("MATH101");

            when(courseMapper.toEntity(inputDto)).thenReturn(mappedCourse);
            when(courseRepository.save(mappedCourse)).thenReturn(savedCourse);
            when(courseMapper.toDto(mappedCourse)).thenReturn(outputDto);

            // Act
            CourseDto result = courseService.createCourse(inputDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Calculus");
            assertThat(result.getCode()).isEqualTo("MATH101");
            verify(courseRepository).save(mappedCourse);
        }

        @Test
        @DisplayName("Create course - should call mapper toEntity and toDto")
        void createCourse_ShouldCallMapper() {
            // Arrange
            CourseDto inputDto = new CourseDto();
            inputDto.setName("Physics");
            inputDto.setCode("PHY101");

            Course entity = new Course();
            when(courseMapper.toEntity(inputDto)).thenReturn(entity);
            when(courseRepository.save(entity)).thenReturn(entity);
            when(courseMapper.toDto(entity)).thenReturn(inputDto);

            // Act
            courseService.createCourse(inputDto);

            // Assert
            verify(courseMapper).toEntity(inputDto);
            verify(courseMapper).toDto(entity);
        }
    }

    // ===================== Assign Teacher to Course =====================

    @Nested
    @DisplayName("Assign Teacher to Course Tests")
    class AssignTeacherTests {

        @Test
        @DisplayName("Assign teacher to course - should set teacher and return updated CourseDto")
        void assignTeacherToCourse_ShouldReturnUpdatedCourseDto() {
            // Arrange
            CourseDto expectedDto = new CourseDto();
            expectedDto.setId(10L);
            expectedDto.setName("Calculus");
            expectedDto.setCode("MATH101");
            expectedDto.setTeacherId(1L);
            expectedDto.setTeacherFullName("Jane Smith");

            when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
            when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

            Course savedCourse = new Course();
            savedCourse.setIdCourse(10L);
            savedCourse.setName("Calculus");
            savedCourse.setCode("MATH101");
            savedCourse.setTeacher(teacher);

            when(courseRepository.save(course)).thenReturn(savedCourse);
            when(courseMapper.toDto(savedCourse)).thenReturn(expectedDto);

            // Act
            CourseDto result = courseService.assignTeacherToCourse(10L, 1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTeacherId()).isEqualTo(1L);
            assertThat(result.getTeacherFullName()).isEqualTo("Jane Smith");
            verify(courseRepository).save(course);
        }

        @Test
        @DisplayName("Assign teacher - course not found should throw RuntimeException")
        void assignTeacherToCourse_CourseNotFound_ShouldThrow() {
            // Arrange
            when(courseRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> courseService.assignTeacherToCourse(999L, 1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Course not found");
        }

        @Test
        @DisplayName("Assign teacher - teacher not found should throw RuntimeException")
        void assignTeacherToCourse_TeacherNotFound_ShouldThrow() {
            // Arrange
            when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
            when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> courseService.assignTeacherToCourse(10L, 999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Teacher not found");
        }
    }

    // ===================== Enroll Student in Course =====================

    @Nested
    @DisplayName("Enroll Student in Course Tests")
    class EnrollStudentTests {

        @Test
        @DisplayName("Enroll student - should add course to student and return success message")
        void enrollStudentInCourse_ShouldReturnSuccessMessage() {
            // Arrange
            when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
            when(studentRepository.findById(2L)).thenReturn(Optional.of(student));

            // Act
            String result = courseService.enrollStudentInCourse(10L, 2L);

            // Assert
            assertThat(result).contains("Student enrolled successfully");
            assertThat(result).contains("studentId=2");
            assertThat(result).contains("courseId=10");
            assertThat(student.getCourses()).contains(course);
            verify(studentRepository).save(student);
        }

        @Test
        @DisplayName("Enroll student - course not found should throw RuntimeException")
        void enrollStudentInCourse_CourseNotFound_ShouldThrow() {
            // Arrange
            when(courseRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> courseService.enrollStudentInCourse(999L, 2L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Course not found");
        }

        @Test
        @DisplayName("Enroll student - student not found should throw RuntimeException")
        void enrollStudentInCourse_StudentNotFound_ShouldThrow() {
            // Arrange
            when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
            when(studentRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> courseService.enrollStudentInCourse(10L, 999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Student not found");
        }

        @Test
        @DisplayName("Enroll student - already enrolled should throw RuntimeException")
        void enrollStudentInCourse_AlreadyEnrolled_ShouldThrow() {
            // Arrange
            student.getCourses().add(course);
            when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
            when(studentRepository.findById(2L)).thenReturn(Optional.of(student));

            // Act & Assert
            assertThatThrownBy(() -> courseService.enrollStudentInCourse(10L, 2L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("already enrolled");
        }

        @Test
        @DisplayName("Enroll student - should save student after enrolling")
        void enrollStudentInCourse_ShouldSaveStudent() {
            // Arrange
            when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
            when(studentRepository.findById(2L)).thenReturn(Optional.of(student));

            // Act
            courseService.enrollStudentInCourse(10L, 2L);

            // Assert
            verify(studentRepository).save(student);
        }
    }
}

