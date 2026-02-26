package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.AbsenceDto.AbsenceDto;
import org.example.smartcompus.model.Absence;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    AbsenceDto toDto(Absence absence);
    Absence toEntity(AbsenceDto absenceDto);
}
