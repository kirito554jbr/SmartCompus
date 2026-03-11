package org.example.smartcompus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.smartcompus.dto.AuthDto.AuthResponseDto;
import org.example.smartcompus.dto.AuthDto.LoginRequestDto;
import org.example.smartcompus.dto.UserDto.UserRequestDto;
import org.example.smartcompus.dto.UserDto.UserResponseDto;
import org.example.smartcompus.model.User;
import org.example.smartcompus.model.enums.UserRole;
import org.example.smartcompus.repository.UserRepository;
import org.example.smartcompus.security.JwtService;
import org.example.smartcompus.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private IUserService userService;
    @Mock private UserRepository userRepository;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthController authController;

    private User adminUser;
    private UserDetails adminUserDetails;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setIdUser(1L);
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEmail("admin@smartcampus.com");
        adminUser.setPassword("encodedPassword");
        adminUser.setRole(UserRole.ROLE_ADMIN);
        adminUser.setEnabeld(true);

        adminUserDetails = new org.springframework.security.core.userdetails.User(
                "admin@smartcampus.com",
                "encodedPassword",
                true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    // ===================== Login Success =====================

    @Test
    @DisplayName("Login with valid credentials - should return AuthResponseDto with tokens")
    void login_ValidCredentials_ShouldReturnTokens() {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto("admin@smartcampus.com", "password123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(adminUserDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken(adminUserDetails)).thenReturn("access-token-123");
        when(jwtService.generateRefreshToken(adminUserDetails)).thenReturn("refresh-token-456");
        when(userRepository.findByEmail("admin@smartcampus.com")).thenReturn(Optional.of(adminUser));

        // Act
        var response = authController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        AuthResponseDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAccessToken()).isEqualTo("access-token-123");
        assertThat(body.getRefreshToken()).isEqualTo("refresh-token-456");
        assertThat(body.getEmail()).isEqualTo("admin@smartcampus.com");
        assertThat(body.getRole()).isEqualTo("ROLE_ADMIN");
    }

    // ===================== Login generates both tokens =====================

    @Test
    @DisplayName("Login - should generate both access and refresh tokens")
    void login_ShouldGenerateBothTokens() {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto("admin@smartcampus.com", "password123");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(adminUserDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("access");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(adminUser));

        // Act
        authController.login(loginRequest);

        // Assert
        verify(jwtService).generateToken(adminUserDetails);
        verify(jwtService).generateRefreshToken(adminUserDetails);
    }

    // ===================== Login with invalid credentials =====================

    @Test
    @DisplayName("Login with invalid credentials - should throw BadCredentialsException")
    void login_InvalidCredentials_ShouldThrowException() {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto("admin@smartcampus.com", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authController.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ===================== Register calls service =====================

    @Test
    @DisplayName("Register - should delegate to userService and return CREATED status")
    void register_ShouldDelegateToServiceAndReturnCreated() {
        // Arrange
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setFirstName("New");
        requestDto.setLastName("User");
        requestDto.setEmail("new@smartcampus.com");
        requestDto.setPassword("pass123");
        requestDto.setRole(UserRole.ROLE_ADMIN);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setIdUser(10L);
        responseDto.setFirstName("New");
        responseDto.setLastName("User");
        responseDto.setEmail("new@smartcampus.com");
        responseDto.setRole(UserRole.ROLE_ADMIN);

        when(userService.registerUser(requestDto)).thenReturn(responseDto);

        // Act
        var response = authController.register(requestDto);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getIdUser()).isEqualTo(10L);
        assertThat(response.getBody().getEmail()).isEqualTo("new@smartcampus.com");
        verify(userService).registerUser(requestDto);
    }

    // ===================== Login authenticates with correct credentials =====================

    @Test
    @DisplayName("Login - should pass correct email and password to AuthenticationManager")
    void login_ShouldAuthenticateWithCorrectCredentials() {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto("test@test.com", "myPassword");

        User testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setRole(UserRole.ROLE_TEACHER);

        UserDetails testUserDetails = new org.springframework.security.core.userdetails.User(
                "test@test.com", "encoded", true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"))
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));

        // Act
        authController.login(loginRequest);

        // Assert
        verify(authenticationManager).authenticate(
                argThat(auth -> {
                    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
                    return token.getPrincipal().equals("test@test.com")
                            && token.getCredentials().equals("myPassword");
                })
        );
    }

    // ===================== Login returns correct role in response =====================

    @Test
    @DisplayName("Login - response should contain the correct user role")
    void login_ShouldReturnCorrectRole() {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto("teacher@test.com", "pass");

        User teacherUser = new User();
        teacherUser.setEmail("teacher@test.com");
        teacherUser.setRole(UserRole.ROLE_TEACHER);

        UserDetails teacherDetails = new org.springframework.security.core.userdetails.User(
                "teacher@test.com", "encoded", true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"))
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(teacherDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("t");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("r");
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(teacherUser));

        // Act
        var response = authController.login(loginRequest);

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRole()).isEqualTo("ROLE_TEACHER");
    }
}

