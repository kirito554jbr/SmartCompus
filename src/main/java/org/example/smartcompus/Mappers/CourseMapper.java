package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.CourseDto.CourseDto;
import org.example.smartcompus.model.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDto toDto(Course course);
    Course toEntity(CourseDto courseDto);
}
