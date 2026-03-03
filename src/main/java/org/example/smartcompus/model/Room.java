package org.example.smartcompus.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Lab 101" or "Amphi A"

    private int capacity;

    private String type; // e.g., "LECTURE", "PRACTICAL", "EXAM"

    @OneToMany(mappedBy = "room")
    private List<Timetable> timetables;
}