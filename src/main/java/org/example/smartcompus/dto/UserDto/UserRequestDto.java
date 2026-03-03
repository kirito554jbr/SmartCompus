package org.example.smartcompus.dto.UserDto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.smartcompus.dto.StudentDto.StudentRequestDto;
import org.example.smartcompus.dto.TeacherDto.TeacherRequestDto;
import org.example.smartcompus.model.enums.UserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role", visible = true, defaultImpl = UserRequestDto.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StudentRequestDto.class, name = "ROLE_STUDENT"),
    @JsonSubTypes.Type(value = TeacherRequestDto.class, name = "ROLE_TEACHER")
})
public class UserRequestDto {
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String password;
    private UserRole role;
}
