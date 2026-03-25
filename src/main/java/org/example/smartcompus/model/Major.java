package org.example.smartcompus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@Table(name = "majors")
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMajor;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "major")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Student> students;
}

