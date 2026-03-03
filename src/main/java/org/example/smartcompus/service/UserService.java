package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.StudentMapper;
import org.example.smartcompus.Mappers.TeacherMapper;
import org.example.smartcompus.Mappers.UserMapper;
import org.example.smartcompus.dto.StudentDto.StudentRequestDto;
import org.example.smartcompus.dto.TeacherDto.TeacherRequestDto;
import org.example.smartcompus.dto.UserDto.UserRequestDto;
import org.example.smartcompus.dto.UserDto.UserResponseDto;
import org.example.smartcompus.exceptions.UserNotFoundException;
import org.example.smartcompus.model.Student;
import org.example.smartcompus.model.Teacher;
import org.example.smartcompus.model.User;
import org.example.smartcompus.model.enums.UserRole;
import org.example.smartcompus.repository.StudentRepository;
import org.example.smartcompus.repository.TeacherRepository;
import org.example.smartcompus.repository.UserRepository;
import org.example.smartcompus.service.interfaces.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public List<UserResponseDto> getAllUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserResponseDto registerUser(UserRequestDto userDto) {
        if (userDto.getRole() == UserRole.ROLE_STUDENT && userDto instanceof StudentRequestDto studentDto) {
            Student student = studentMapper.toEntityRequest(studentDto);
            student.setPassword(passwordEncoder.encode(student.getPassword()));
            return studentMapper.toDto(studentRepository.save(student));
        }

        if (userDto.getRole() == UserRole.ROLE_TEACHER && userDto instanceof TeacherRequestDto teacherDto) {
            Teacher teacher = teacherMapper.toEntityRequest(teacherDto);
            teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
            return teacherMapper.toDto(teacherRepository.save(teacher));
        }

        // ROLE_ADMIN or ROLE_ADMIN_STAFF
        User user = userMapper.toEntityRequest(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateProfile(Long id, UserRequestDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot update: User " + id + " not found"));

        userMapper.updateUserFromDto(userDto, existingUser);

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        return userMapper.toDto(userRepository.save(existingUser));
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user); // Explicit save is safer
    }
    //done
}
