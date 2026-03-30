package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.TeacherDto.TeacherResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ITeacherService {
    TeacherResponseDto getTeacherByEmployeeNumber(String employeeNumber);
    List<TeacherResponseDto> getTeachersBySpeciality(String speciality);
    List<TeacherResponseDto> getAllTeachers();
    Page<TeacherResponseDto> getTeachersPaginated(int page, int size, String sortBy, String sortDirection);
}

