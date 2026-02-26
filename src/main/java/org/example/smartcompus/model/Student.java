package org.example.smartcompus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Student extends  User {
    private String studentNumber;
    private String major;

    @OneToMany(mappedBy = "student")
    private List<Absence> absences;

    @OneToMany(mappedBy = "student")
    private List<Request> requests;
}
