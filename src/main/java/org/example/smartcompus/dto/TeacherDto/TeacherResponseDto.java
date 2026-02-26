package org.example.smartcompus.dto.TeacherDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.smartcompus.dto.UserDto.UserResponseDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherResponseDto extends UserResponseDto {
    private String employeeNumber;
    private String speciality;
}
