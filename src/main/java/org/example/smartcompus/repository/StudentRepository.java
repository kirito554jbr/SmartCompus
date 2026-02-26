package org.example.smartcompus.repository;

import org.example.smartcompus.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Find student by their unique student number
    Optional<Student> findByStudentNumber(String studentNumber);
}