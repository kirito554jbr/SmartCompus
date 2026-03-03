package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.TeacherMapper;
import org.example.smartcompus.dto.TeacherDto.TeacherResponseDto;
import org.example.smartcompus.exceptions.ResourceNotFoundException;
import org.example.smartcompus.repository.TeacherRepository;
import org.example.smartcompus.service.interfaces.ITeacherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService implements ITeacherService {
    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;


    @Override
    public TeacherResponseDto getTeacherByEmployeeNumber(String employeeNumber) {
        return teacherRepository.findTeacherByEmployeeNumber(employeeNumber)
                .map(teacherMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher with " + employeeNumber + "not found"));
    }

    @Override
    public List<TeacherResponseDto> getTeachersBySpeciality(String speciality) {
        return teacherRepository.findBySpeciality(speciality).stream()
                .map(teacherMapper::toDto)
                .toList();
    }
}
