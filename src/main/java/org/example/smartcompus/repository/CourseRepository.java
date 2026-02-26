package org.example.smartcompus.repository;

import org.example.smartcompus.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // Find all courses taught by a specific teacher
    List<Course> findByTeacher_IdUser(Long teacherId);
}

