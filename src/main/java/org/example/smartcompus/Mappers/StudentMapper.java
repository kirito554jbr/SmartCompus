package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.StudentDto.StudentRequestDto;
import org.example.smartcompus.dto.StudentDto.StudentResponseDto;
import org.example.smartcompus.model.Student;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    StudentResponseDto toDto(Student student);
    Student toEntityRequest(StudentRequestDto studentRequestDto);
    Student toEntityResponse(StudentResponseDto studentResponseDto);
}
