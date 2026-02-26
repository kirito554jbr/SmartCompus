package org.example.smartcompus.dto.StudentDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.smartcompus.dto.UserDto.UserRequestDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentRequestDto extends UserRequestDto {
    private String studentNumber;
    private String major;
}
