package org.example.smartcompus.repository;

import org.example.smartcompus.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByDay(String day);
    List<Timetable> findByCourse_Teacher_IdUser(Long teacherId);

    @Query("SELECT t FROM Timetable t JOIN t.course c JOIN c.students s WHERE s.idUser = :studentId")
    List<Timetable> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(t) > 0 FROM Timetable t " +
            "WHERE t.room.id = :roomId " +
            "AND t.date = :date " +
            "AND t.day = :day " +
            "AND t.startTime < :endTime " +
            "AND t.endTime > :startTime")
    boolean existsOverlap(@Param("roomId") Long roomId,
                          @Param("date") LocalDate date,
                          @Param("day") String day,
                          @Param("startTime") LocalTime startTime,
                          @Param("endTime") LocalTime endTime);

}