package org.example.smartcompus.dto.AbsenceDto;

import lombok.Data;
import org.example.smartcompus.model.enums.AbsenceStatus;

import java.time.LocalDate;

@Data
public class AbsenceDto {
    private Long id;
    private LocalDate date;
    private AbsenceStatus status; // JUSTIFIED, UNJUSTIFIED, PENDING
    private Long studentId;
    private String studentFullName;
}
