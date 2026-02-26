package org.example.smartcompus.dto.UserDto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.smartcompus.model.enums.UserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private long idUser;
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private UserRole role;
}