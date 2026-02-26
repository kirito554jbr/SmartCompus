package org.example.smartcompus.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import org.example.smartcompus.model.enums.AbsenceStatus;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "absences")
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private AbsenceStatus status;

    @ManyToOne @JoinColumn(name = "student_id")
    private Student student;
}
