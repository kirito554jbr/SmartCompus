package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.TeacherDto.TeacherResponseDto;

import java.util.List;

public interface ITeacherService {
    TeacherResponseDto getTeacherByEmployeeNumber(String employeeNumber);
    List<TeacherResponseDto> getTeachersBySpeciality(String speciality);
}

