package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.TimetableDto.TimetableDto;
import org.example.smartcompus.model.Timetable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TimetableMapper {
    TimetableDto toDto(Timetable timetable);
    Timetable toEntity(TimetableDto timetableDto);
}
