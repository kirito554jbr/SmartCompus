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
@DiscriminatorValue("TEACHER")
public class Teacher extends  User {

    private String employeeNumber;
    private String speciality;

    @OneToMany(mappedBy = "teacher")
    private List<Course> courses;
}
