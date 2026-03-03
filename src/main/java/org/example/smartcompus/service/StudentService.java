package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.StudentMapper;
import org.example.smartcompus.dto.StudentDto.StudentResponseDto;
import org.example.smartcompus.exceptions.ResourceNotFoundException;
import org.example.smartcompus.repository.StudentRepository;
import org.example.smartcompus.service.interfaces.IStudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService implements IStudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;


    @Override
    public StudentResponseDto getStudentByNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Student with " + studentNumber + "not found"));

    }

    @Override
    public List<StudentResponseDto> getStudentsByMajor(String major) {
        return studentRepository.findStudentsByMajor(major).stream()
                .map(studentMapper::toDto)
                .toList();
    }
}
