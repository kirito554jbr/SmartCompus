package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.RequestDto.RequestDto;
import org.example.smartcompus.model.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "student.idUser", target = "studentId")
    RequestDto toDto(Request absence);
    @Mapping(source = "studentId", target = "student.idUser")
    Request toEntity(RequestDto absenceDto);
}
