package org.example.smartcompus.service;

import org.example.smartcompus.Mappers.StudentMapper;
import org.example.smartcompus.Mappers.TeacherMapper;
import org.example.smartcompus.Mappers.UserMapper;
import org.example.smartcompus.dto.StudentDto.StudentRequestDto;
import org.example.smartcompus.dto.StudentDto.StudentResponseDto;
import org.example.smartcompus.dto.TeacherDto.TeacherRequestDto;
import org.example.smartcompus.dto.TeacherDto.TeacherResponseDto;
import org.example.smartcompus.dto.UserDto.UserRequestDto;
import org.example.smartcompus.dto.UserDto.UserResponseDto;
import org.example.smartcompus.model.Student;
import org.example.smartcompus.model.Teacher;
import org.example.smartcompus.model.User;
import org.example.smartcompus.model.enums.UserRole;
import org.example.smartcompus.repository.StudentRepository;
import org.example.smartcompus.repository.TeacherRepository;
import org.example.smartcompus.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private TeacherRepository teacherRepository;
    @Mock private UserMapper userMapper;
    @Mock private StudentMapper studentMapper;
    @Mock private TeacherMapper teacherMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ===================== Register Admin User =====================

    @Test
    @DisplayName("Register admin user - should save and return UserResponseDto")
    void registerUser_AdminRole_ShouldReturnUserResponseDto() {
        // Arrange
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setFirstName("Admin");
        requestDto.setLastName("User");
        requestDto.setEmail("admin@smartcampus.com");
        requestDto.setPassword("password123");
        requestDto.setRole(UserRole.ROLE_ADMIN);

        User userEntity = new User();
        userEntity.setFirstName("Admin");
        userEntity.setLastName("User");
        userEntity.setEmail("admin@smartcampus.com");
        userEntity.setPassword("password123");
        userEntity.setRole(UserRole.ROLE_ADMIN);

        User savedUser = new User();
        savedUser.setIdUser(1L);
        savedUser.setFirstName("Admin");
        savedUser.setLastName("User");
        savedUser.setEmail("admin@smartcampus.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(UserRole.ROLE_ADMIN);
        savedUser.setEnabeld(true);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setIdUser(1L);
        responseDto.setFirstName("Admin");
        responseDto.setLastName("User");
        responseDto.setEmail("admin@smartcampus.com");
        responseDto.setRole(UserRole.ROLE_ADMIN);

        when(userMapper.toEntityRequest(requestDto)).thenReturn(userEntity);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDto);

        // Act
        UserResponseDto result = userService.registerUser(requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIdUser()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("admin@smartcampus.com");
        assertThat(result.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    // ===================== Register Student =====================

    @Test
    @DisplayName("Register student - should save Student and return StudentResponseDto")
    void registerUser_StudentRole_ShouldReturnStudentResponseDto() {
        // Arrange
        StudentRequestDto requestDto = new StudentRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setEmail("john.doe@student.com");
        requestDto.setPassword("studentPass");
        requestDto.setRole(UserRole.ROLE_STUDENT);
        requestDto.setStudentNumber("STU001");
        requestDto.setMajor("Computer Science");

        Student studentEntity = new Student();
        studentEntity.setFirstName("John");
        studentEntity.setLastName("Doe");
        studentEntity.setEmail("john.doe@student.com");
        studentEntity.setPassword("studentPass");
        studentEntity.setRole(UserRole.ROLE_STUDENT);
        studentEntity.setStudentNumber("STU001");
        studentEntity.setMajor("Computer Science");

        Student savedStudent = new Student();
        savedStudent.setIdUser(2L);
        savedStudent.setFirstName("John");
        savedStudent.setLastName("Doe");
        savedStudent.setEmail("john.doe@student.com");
        savedStudent.setPassword("encodedPassword");
        savedStudent.setRole(UserRole.ROLE_STUDENT);
        savedStudent.setStudentNumber("STU001");
        savedStudent.setMajor("Computer Science");
        savedStudent.setEnabeld(true);

        StudentResponseDto responseDto = new StudentResponseDto();
        responseDto.setIdUser(2L);
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");
        responseDto.setEmail("john.doe@student.com");
        responseDto.setRole(UserRole.ROLE_STUDENT);
        responseDto.setStudentNumber("STU001");
        responseDto.setMajor("Computer Science");

        when(studentMapper.toEntityRequest(requestDto)).thenReturn(studentEntity);
        when(passwordEncoder.encode("studentPass")).thenReturn("encodedPassword");
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);
        when(studentMapper.toDto(savedStudent)).thenReturn(responseDto);

        // Act
        UserResponseDto result = userService.registerUser(requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(StudentResponseDto.class);
        assertThat(result.getIdUser()).isEqualTo(2L);
        assertThat(result.getEmail()).isEqualTo("john.doe@student.com");
        assertThat(result.getRole()).isEqualTo(UserRole.ROLE_STUDENT);
        assertThat(((StudentResponseDto) result).getStudentNumber()).isEqualTo("STU001");
        verify(studentRepository).save(any(Student.class));
        verify(userRepository, never()).save(any());
    }

    // ===================== Register Teacher =====================

    @Test
    @DisplayName("Register teacher - should save Teacher and return TeacherResponseDto")
    void registerUser_TeacherRole_ShouldReturnTeacherResponseDto() {
        // Arrange
        TeacherRequestDto requestDto = new TeacherRequestDto();
        requestDto.setFirstName("Jane");
        requestDto.setLastName("Smith");
        requestDto.setEmail("jane.smith@teacher.com");
        requestDto.setPassword("teacherPass");
        requestDto.setRole(UserRole.ROLE_TEACHER);
        requestDto.setEmployeeNumber("EMP001");
        requestDto.setSpeciality("Mathematics");

        Teacher teacherEntity = new Teacher();
        teacherEntity.setFirstName("Jane");
        teacherEntity.setLastName("Smith");
        teacherEntity.setEmail("jane.smith@teacher.com");
        teacherEntity.setPassword("teacherPass");
        teacherEntity.setRole(UserRole.ROLE_TEACHER);
        teacherEntity.setEmployeeNumber("EMP001");
        teacherEntity.setSpeciality("Mathematics");

        Teacher savedTeacher = new Teacher();
        savedTeacher.setIdUser(3L);
        savedTeacher.setFirstName("Jane");
        savedTeacher.setLastName("Smith");
        savedTeacher.setEmail("jane.smith@teacher.com");
        savedTeacher.setPassword("encodedPassword");
        savedTeacher.setRole(UserRole.ROLE_TEACHER);
        savedTeacher.setEmployeeNumber("EMP001");
        savedTeacher.setSpeciality("Mathematics");
        savedTeacher.setEnabeld(true);

        TeacherResponseDto responseDto = new TeacherResponseDto();
        responseDto.setIdUser(3L);
        responseDto.setFirstName("Jane");
        responseDto.setLastName("Smith");
        responseDto.setEmail("jane.smith@teacher.com");
        responseDto.setRole(UserRole.ROLE_TEACHER);
        responseDto.setEmployeeNumber("EMP001");
        responseDto.setSpeciality("Mathematics");

        when(teacherMapper.toEntityRequest(requestDto)).thenReturn(teacherEntity);
        when(passwordEncoder.encode("teacherPass")).thenReturn("encodedPassword");
        when(teacherRepository.save(any(Teacher.class))).thenReturn(savedTeacher);
        when(teacherMapper.toDto(savedTeacher)).thenReturn(responseDto);

        // Act
        UserResponseDto result = userService.registerUser(requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(TeacherResponseDto.class);
        assertThat(result.getIdUser()).isEqualTo(3L);
        assertThat(result.getRole()).isEqualTo(UserRole.ROLE_TEACHER);
        assertThat(((TeacherResponseDto) result).getEmployeeNumber()).isEqualTo("EMP001");
        verify(teacherRepository).save(any(Teacher.class));
        verify(userRepository, never()).save(any());
    }

    // ===================== Register encodes password =====================

    @Test
    @DisplayName("Register user - should encode password before saving")
    void registerUser_ShouldEncodePassword() {
        // Arrange
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setFirstName("Staff");
        requestDto.setLastName("Member");
        requestDto.setEmail("staff@smartcampus.com");
        requestDto.setPassword("rawPassword");
        requestDto.setRole(UserRole.ROLE_ADMIN_STAFF);

        User userEntity = new User();
        userEntity.setPassword("rawPassword");
        userEntity.setRole(UserRole.ROLE_ADMIN_STAFF);

        User savedUser = new User();
        savedUser.setIdUser(4L);
        savedUser.setPassword("encodedRawPassword");
        savedUser.setEnabeld(true);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setIdUser(4L);

        when(userMapper.toEntityRequest(requestDto)).thenReturn(userEntity);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedRawPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDto);

        // Act
        userService.registerUser(requestDto);

        // Assert
        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(argThat(user -> user.getPassword().equals("encodedRawPassword")));
    }

    // ===================== Register sets account enabled =====================

    @Test
    @DisplayName("Register user - should set account enabled to true")
    void registerUser_ShouldSetAccountEnabled() {
        // Arrange
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setFirstName("New");
        requestDto.setLastName("User");
        requestDto.setEmail("new@smartcampus.com");
        requestDto.setPassword("pass");
        requestDto.setRole(UserRole.ROLE_ADMIN);

        User userEntity = new User();
        userEntity.setRole(UserRole.ROLE_ADMIN);
        userEntity.setPassword("pass");

        User savedUser = new User();
        savedUser.setIdUser(5L);
        savedUser.setEnabeld(true);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setIdUser(5L);

        when(userMapper.toEntityRequest(requestDto)).thenReturn(userEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDto);

        // Act
        userService.registerUser(requestDto);

        // Assert
        verify(userRepository).save(argThat(User::isEnabeld));
    }
}

