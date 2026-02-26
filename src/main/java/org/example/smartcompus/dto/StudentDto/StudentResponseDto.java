package org.example.smartcompus.dto.StudentDto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.smartcompus.dto.UserDto.UserResponseDto;

@Data
@EqualsAndHashCode(callSuper=true)
public class StudentResponseDto extends UserResponseDto {
    private String studentNumber;
    private String major;
}
