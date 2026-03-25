package org.example.smartcompus.service.interfaces;

import org.example.smartcompus.dto.UserDto.ChangePasswordRequestDto;
import org.example.smartcompus.dto.UserDto.UserRequestDto;
import org.example.smartcompus.dto.UserDto.UserResponseDto;
import org.example.smartcompus.model.enums.UserRole;

import java.util.List;

public interface IUserService {
    UserResponseDto registerUser(UserRequestDto userDto);
    UserResponseDto updateProfile(Long id, UserRequestDto userDto);
    void changePassword(Long id, ChangePasswordRequestDto request);
    List<UserResponseDto> getAllUsers();
    List<UserResponseDto> getAllUsersByRole(UserRole role);
    UserResponseDto getUserById(Long id);
    UserResponseDto getUserByEmail(String email);
    void deleteUser(Long id);
}
