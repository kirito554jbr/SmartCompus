package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.StudentDto.StudentRequestDto;
import org.example.smartcompus.dto.StudentDto.StudentResponseDto;
import org.example.smartcompus.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(target = "major", source = "major.name")
    StudentResponseDto toDto(Student student);

    @Mapping(target = "major", ignore = true)
    Student toEntityRequest(StudentRequestDto studentRequestDto);

    @Mapping(target = "major", ignore = true)
    Student toEntityResponse(StudentResponseDto studentResponseDto);
}
