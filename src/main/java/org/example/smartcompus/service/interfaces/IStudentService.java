package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.StudentDto.StudentResponseDto;

import java.util.List;

public interface IStudentService {
    StudentResponseDto getStudentByNumber(String studentNumber);
    List<StudentResponseDto> getStudentsByMajor(String major);
}