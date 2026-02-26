package org.example.smartcompus.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "timeTables")
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idTimetable;
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne @JoinColumn(name = "course_id")
    private Course course;
}
