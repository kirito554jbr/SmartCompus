package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.TimetableDto.TimetableDto;
import org.example.smartcompus.model.Timetable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimetableMapper {

    @Mapping(source = "idTimetable", target = "id")
    @Mapping(source = "course.idCourse", target = "courseId")
    @Mapping(source = "course.name", target = "courseName")
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.name", target = "roomName")
    TimetableDto toDto(Timetable timetable);

    @Mapping(target = "idTimetable", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "room", ignore = true)
    Timetable toEntity(TimetableDto timetableDto);
}
