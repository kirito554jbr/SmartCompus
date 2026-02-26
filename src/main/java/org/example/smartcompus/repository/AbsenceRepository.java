package org.example.smartcompus.repository;

import org.example.smartcompus.model.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    // Track absences for a student
    List<Absence> findByStudent_IdUser(Long studentId);
}