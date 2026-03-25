package org.example.smartcompus.repository;

import org.example.smartcompus.model.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
    Optional<Major> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
