package org.example.smartcompus.dto.TeacherDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.smartcompus.dto.UserDto.UserRequestDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherRequestDto extends UserRequestDto {
    private String employeeNumber;
    private String speciality;
}
