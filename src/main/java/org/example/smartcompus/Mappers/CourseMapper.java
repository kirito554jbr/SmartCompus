package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.CourseDto.CourseDto;
import org.example.smartcompus.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {
//    @Mapping(source = "teacher.idUser", target = "teacherId")
//    @Mapping(target = "teacherFullName", expression = "java(course.getTeacher() != null ? course.getTeacher().getFirstName() + \" \" + course.getTeacher().getLastName() : null)")
    CourseDto toDto(Course course);

    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "timetables", ignore = true)
    Course toEntity(CourseDto courseDto);
}
