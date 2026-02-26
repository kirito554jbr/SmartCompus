package org.example.smartcompus.service;

import lombok.RequiredArgsConstructor;
import org.example.smartcompus.Mappers.AbsenceMapper;
import org.example.smartcompus.dto.AbsenceDto.AbsenceDto;
import org.example.smartcompus.exceptions.ResourceNotFoundException;
import org.example.smartcompus.model.Absence;
import org.example.smartcompus.model.Student;
import org.example.smartcompus.model.enums.AbsenceStatus;
import org.example.smartcompus.repository.AbsenceRepository;
import org.example.smartcompus.repository.StudentRepository;
import org.example.smartcompus.service.interfaces.IAbsenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AbsenceService implements IAbsenceService {
    private final AbsenceRepository absenceRepository;
    private final StudentRepository studentRepository;
    private final AbsenceMapper  absenceMapper;

    @Override
    @Transactional // Important to ensure the DB operation is atomic
    public AbsenceDto markStudentAbsent(AbsenceDto dto) {
        // 1. Verify that the student exists in the database
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + dto.getStudentId()));

        // 2. Map the incoming DTO to an Absence Entity
        Absence absence = absenceMapper.toEntity(dto);

        // 3. Manually link the Student entity to the Absence
        // (MapStruct usually only maps IDs, so we set the object here)
        absence.setStudent(student);

        // 4. Set default status if not provided (e.g., PENDING or UNJUSTIFIED)
        if (absence.getStatus() == null) {
            absence.setStatus(AbsenceStatus.PENDING);
        }

        // 5. Save to database and map the result back to a DTO for the frontend
        Absence savedAbsence = absenceRepository.save(absence);
        return absenceMapper.toDto(savedAbsence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbsenceDto> getAbsencesByStudent(Long studentId) {
        return absenceRepository.findByStudent_IdUser(studentId).stream()
                .map(absenceMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AbsenceDto justifyAbsence(Long absenceId, boolean isAccepted) {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Absence not found with ID: " + absenceId));

        AbsenceStatus newStatus = isAccepted ? AbsenceStatus.JUSTIFIED : AbsenceStatus.UNJUSTIFIED;
        absence.setStatus(newStatus);

        return absenceMapper.toDto(absenceRepository.save(absence));
    }

    @Override
    public List<AbsenceDto> getAllAbsences() {
        return absenceRepository.findAll().stream()
                .map(absenceMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AbsenceDto getAbsenceById(Long id) {
        return absenceRepository.findById(id)
                .map(absenceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Absence " + id + " not found")); // Standardized
    }

    @Override
    public void deleteAbsence(Long id) {
        if (!absenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Absence " + id + " not found");
        }
        absenceRepository.deleteById(id);
    }
}
