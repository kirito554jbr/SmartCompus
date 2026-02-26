package org.example.smartcompus.repository;

import org.example.smartcompus.model.Request;
import org.example.smartcompus.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    // Filter requests by status (e.g., PENDING)
    List<Request> findByStatus(RequestStatus status);

    // Get all requests for a specific student
    List<Request> findByStudent_IdUser(Long studentId);
}


