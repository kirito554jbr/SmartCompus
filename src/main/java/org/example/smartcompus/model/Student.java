package org.example.smartcompus.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends  User {
    private String studentNumber;

    @ManyToOne
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

    @OneToMany(mappedBy = "student")
    private List<Absence> absences;

    @OneToMany(mappedBy = "student")
    private List<Request> requests;
}
