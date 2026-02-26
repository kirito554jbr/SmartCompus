package org.example.smartcompus.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class Teacher extends  User {

    private int employeeNumber;
    private String speciality;

    @OneToMany(mappedBy = "teacher")
    private List<Course> courses;
}
