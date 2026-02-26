package org.example.smartcompus.repository;

import org.example.smartcompus.dto.UserDto.UserResponseDto;
import org.example.smartcompus.model.User;
import org.example.smartcompus.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
}

