package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.StudentDto.StudentRequestDto;
import org.example.smartcompus.dto.StudentDto.StudentResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IStudentService {
    StudentResponseDto getStudentByNumber(String studentNumber);
    List<StudentResponseDto> getStudentsByMajor(String major);
    List<StudentResponseDto> getAllStudents();
    Page<StudentResponseDto> getStudentsPaginated(int page, int size, String sortBy, String sortDirection);
    StudentResponseDto updateStudentMajor(String studentNumber, StudentRequestDto requestDto);
}