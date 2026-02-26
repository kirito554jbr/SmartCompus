package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.AbsenceDto.AbsenceDto;

import java.util.List;

public interface IAbsenceService {
    AbsenceDto markStudentAbsent(AbsenceDto dto);
    List<AbsenceDto> getAbsencesByStudent(Long studentId);
    AbsenceDto justifyAbsence(Long absenceId, String reason);
    List<AbsenceDto> getAllAbsences();
    AbsenceDto getAbsenceById(Long id);
    void deleteAbsence(Long id);
}

