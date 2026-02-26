package org.example.smartcompus.dto.RequestDto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestDto {
    private Long id;
    private String type;   // ENROLLMENT_CERTIFICATE, TRANSCRIPT, etc.
    private String description;
    private String status; // PENDING, APPROVED, REJECTED
    private LocalDate createdAt;
    private Long studentId;
    private String studentFullName;
}
