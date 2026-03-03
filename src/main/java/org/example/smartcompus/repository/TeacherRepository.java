package org.example.smartcompus.repository;

import org.example.smartcompus.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    // Find teachers by their specific speciality
    List<Teacher> findBySpeciality(String speciality);

    Optional<Teacher> findTeacherByEmployeeNumber(String employeeNumber);
}