package org.example.smartcompus.Mappers;

import org.example.smartcompus.dto.UserDto.UserRequestDto;
import org.example.smartcompus.dto.UserDto.UserResponseDto;
import org.example.smartcompus.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);
    User toEntityResponse(UserResponseDto userResponseDto);
    User toEntityRequest(UserRequestDto userRequestDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserRequestDto dto, @MappingTarget User entity);
}