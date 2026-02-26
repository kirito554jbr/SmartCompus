package org.example.smartcompus.repository;

import org.example.smartcompus.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    // Get schedule for a specific day
    List<Timetable> findByDay(String day);

    Optional<Timetable> findByStudentId(Long studentId);

}