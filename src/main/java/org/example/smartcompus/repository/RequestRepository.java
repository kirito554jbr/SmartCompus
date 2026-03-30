package org.example.smartcompus.repository;

import org.example.smartcompus.model.Request;
import org.example.smartcompus.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStatus(RequestStatus status);

    List<Request> findByStudent_IdUser(Long studentId);
}


