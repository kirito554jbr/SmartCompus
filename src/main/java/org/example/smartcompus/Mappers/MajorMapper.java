package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.MajorDto.MajorDto;
import org.example.smartcompus.model.Major;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface MajorMapper {
    @Mapping(source = "idMajor", target = "id")
    MajorDto toDto(Major major);

    @Mapping(source = "id", target = "idMajor")
    @Mapping(target = "students", ignore = true)
    Major toEntity(MajorDto majorDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idMajor", ignore = true)
    @Mapping(target = "students", ignore = true)
    void updateMajorFromDto(MajorDto dto, @MappingTarget Major entity);
}
