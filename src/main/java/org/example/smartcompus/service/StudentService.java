package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.StudentMapper;
import org.example.smartcompus.dto.StudentDto.StudentRequestDto;
import org.example.smartcompus.dto.StudentDto.StudentResponseDto;
import org.example.smartcompus.exceptions.ResourceNotFoundException;
import org.example.smartcompus.model.Major;
import org.example.smartcompus.model.Student;
import org.example.smartcompus.repository.MajorRepository;
import org.example.smartcompus.repository.StudentRepository;
import org.example.smartcompus.service.interfaces.IStudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService implements IStudentService {
    private final StudentRepository studentRepository;
    private final MajorRepository majorRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponseDto getStudentByNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Student with " + studentNumber + " not found"));

    }

    @Override
    public List<StudentResponseDto> getStudentsByMajor(String major) {
        String normalizedMajor = major.trim();
        Major existingMajor = majorRepository.findByNameIgnoreCase(normalizedMajor)
                .orElseThrow(() -> new ResourceNotFoundException("Major " + normalizedMajor + " not found"));

        return studentRepository.findStudentsByMajor(existingMajor).stream()
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    public List<StudentResponseDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    public Page<StudentResponseDto> getStudentsPaginated(int page, int size, String sortBy, String sortDirection) {
        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return studentRepository.findAll(PageRequest.of(page, size, sort))
                .map(studentMapper::toDto);
    }

    @Override
    public StudentResponseDto updateStudentMajor(String studentNumber, StudentRequestDto requestDto) {
        if (requestDto == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        Student existingStudent = studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Student with " + studentNumber + " not found"));

        Major targetMajor;
        if (requestDto.getMajorId() != null) {
            targetMajor = majorRepository.findById(requestDto.getMajorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Major " + requestDto.getMajorId() + " not found"));
        } else {
            String normalizedMajor = requestDto.getMajor() == null ? "" : requestDto.getMajor().trim();
            if (normalizedMajor.isBlank()) {
                throw new IllegalArgumentException("Provide majorId or major name");
            }

            targetMajor = majorRepository.findByNameIgnoreCase(normalizedMajor)
                    .orElseThrow(() -> new ResourceNotFoundException("Major " + normalizedMajor + " not found"));
        }

        existingStudent.setMajor(targetMajor);
        return studentMapper.toDto(studentRepository.save(existingStudent));
    }


}
