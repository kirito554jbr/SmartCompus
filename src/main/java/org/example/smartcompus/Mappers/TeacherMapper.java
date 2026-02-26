package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.TeacherDto.TeacherRequestDto;
import org.example.smartcompus.dto.TeacherDto.TeacherResponseDto;
import org.example.smartcompus.model.Teacher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    TeacherResponseDto toDto(Teacher teacher);
    Teacher toEntityRequest(TeacherRequestDto teacherRequestDto);
    Teacher toEntityResponse(TeacherResponseDto teacherResponseDto);
}
