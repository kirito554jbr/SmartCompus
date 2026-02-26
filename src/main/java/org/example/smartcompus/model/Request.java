package org.example.smartcompus.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import org.example.smartcompus.model.enums.RequestStatus;
import org.example.smartcompus.model.enums.RequestType;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRequest;
    @Enumerated(EnumType.STRING)
    private RequestType type;
    private String description;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    private LocalDate createdAt;

    @ManyToOne @JoinColumn(name = "student_id")
    private Student student;
}
