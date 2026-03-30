package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.AbsenceDto.AbsenceDto;
import org.example.smartcompus.model.Absence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AbsenceMapper {

    @Mapping(source = "student.idUser", target = "studentId")
    @Mapping(target = "studentFullName", expression = "java(absence.getStudent() != null ? absence.getStudent().getFirstName() + \" \" + absence.getStudent().getLastName() : null)")
    AbsenceDto toDto(Absence absence);

    @Mapping(source = "studentId", target = "student.idUser")
    @Mapping(target = "student", ignore = true)
    Absence toEntity(AbsenceDto absenceDto);
}
