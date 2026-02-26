package org.example.smartcompus.dto.AbsenceDto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AbsenceDto {
    private Long id;
    private LocalDate date;
    private String status; // JUSTIFIED, UNJUSTIFIED, PENDING
    private Long studentId;
    private String studentFullName;
}
