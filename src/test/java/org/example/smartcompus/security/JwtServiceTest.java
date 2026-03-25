package org.example.smartcompus.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.smartcompus.model.User;
import org.example.smartcompus.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET_KEY = "dGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JKV1RUb2tlbkdlbmVyYXRpb24xMjM0NTY3ODk=";
    private static final long ACCESS_EXPIRATION = 900000;
    private static final long REFRESH_EXPIRATION = 604800000;

    private User adminUser;
    private User teacherUser;
    private User studentUser;

    private UserDetails adminUserDetails;
    private UserDetails teacherUserDetails;
    private UserDetails studentUserDetails;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        setField(jwtService, "secretKey", SECRET_KEY);
        setField(jwtService, "jwtExpiration", ACCESS_EXPIRATION);
        setField(jwtService, "refreshExpiration", REFRESH_EXPIRATION);

        adminUser = new User();
        adminUser.setIdUser(1L);
        adminUser.setFirstName("Admin");
        adminUser.setLastName("Boss");
        adminUser.setEmail("admin@smartcampus.com");
        adminUser.setPassword("encoded");
        adminUser.setRole(UserRole.ROLE_ADMIN);

        teacherUser = new User();
        teacherUser.setIdUser(2L);
        teacherUser.setFirstName("Jane");
        teacherUser.setLastName("Smith");
        teacherUser.setEmail("teacher@smartcampus.com");
        teacherUser.setPassword("encoded");
        teacherUser.setRole(UserRole.ROLE_TEACHER);

        studentUser = new User();
        studentUser.setIdUser(3L);
        studentUser.setFirstName("John");
        studentUser.setLastName("Doe");
        studentUser.setEmail("student@smartcampus.com");
        studentUser.setPassword("encoded");
        studentUser.setRole(UserRole.ROLE_STUDENT);

        adminUserDetails = new org.springframework.security.core.userdetails.User(
                "admin@smartcampus.com", "encoded", true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        teacherUserDetails = new org.springframework.security.core.userdetails.User(
                "teacher@smartcampus.com", "encoded", true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER")));
        studentUserDetails = new org.springframework.security.core.userdetails.User(
                "student@smartcampus.com", "encoded", true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")));
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ===================== Access Token Generation =====================

    @Nested
    @DisplayName("Access Token Generation Tests")
    class AccessTokenGenerationTests {

        @Test
        @DisplayName("generateAccessToken - should return a non-null, non-empty JWT")
        void generateAccessToken_ShouldReturnNonEmptyString() {
            String token = jwtService.generateAccessToken(adminUser);
            assertThat(token).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("generateAccessToken - subject should be the user email")
        void generateAccessToken_ShouldContainCorrectSubject() {
            String token = jwtService.generateAccessToken(adminUser);
            assertThat(parseClaims(token).getSubject()).isEqualTo("admin@smartcampus.com");
        }

        @Test
        @DisplayName("generateAccessToken - should contain role claim")
        void generateAccessToken_ShouldContainRoleClaim() {
            String token = jwtService.generateAccessToken(teacherUser);
            assertThat(parseClaims(token).get("role", String.class)).isEqualTo("ROLE_TEACHER");
        }

        @Test
        @DisplayName("generateAccessToken - should contain userId claim")
        void generateAccessToken_ShouldContainUserIdClaim() {
            String token = jwtService.generateAccessToken(studentUser);
            assertThat(parseClaims(token).get("userId", Long.class)).isEqualTo(3L);
        }

        @Test
        @DisplayName("generateAccessToken - should contain firstName and lastName claims")
        void generateAccessToken_ShouldContainNameClaims() {
            String token = jwtService.generateAccessToken(teacherUser);
            Claims claims = parseClaims(token);
            assertThat(claims.get("firstName", String.class)).isEqualTo("Jane");
            assertThat(claims.get("lastName", String.class)).isEqualTo("Smith");
        }

        @Test
        @DisplayName("generateAccessToken - should have a future expiration date")
        void generateAccessToken_ShouldHaveFutureExpiration() {
            String token = jwtService.generateAccessToken(adminUser);
            assertThat(parseClaims(token).getExpiration()).isAfter(new Date());
        }

        @Test
        @DisplayName("generateAccessToken - different users should produce different tokens")
        void generateAccessToken_DifferentUsers_ShouldProduceDifferentTokens() {
            String adminToken = jwtService.generateAccessToken(adminUser);
            String teacherToken = jwtService.generateAccessToken(teacherUser);
            assertThat(adminToken).isNotEqualTo(teacherToken);
        }
    }

    // ===================== Refresh Token Generation =====================

    @Nested
    @DisplayName("Refresh Token Generation Tests")
    class RefreshTokenGenerationTests {

        @Test
        @DisplayName("generateRefreshToken - should return a valid token with correct subject")
        void generateRefreshToken_ShouldReturnValidToken() {
            String refreshToken = jwtService.generateRefreshToken(adminUser);
            assertThat(refreshToken).isNotNull().isNotEmpty();
            assertThat(parseClaims(refreshToken).getSubject()).isEqualTo("admin@smartcampus.com");
        }

        @Test
        @DisplayName("generateRefreshToken - should NOT contain role/userId claims")
        void generateRefreshToken_ShouldNotContainExtraClaims() {
            String refreshToken = jwtService.generateRefreshToken(adminUser);
            Claims claims = parseClaims(refreshToken);
            assertThat(claims.get("role")).isNull();
            assertThat(claims.get("userId")).isNull();
        }

        @Test
        @DisplayName("generateRefreshToken - should have longer expiration than access token")
        void generateRefreshToken_ShouldHaveLongerExpiration() {
            String accessToken = jwtService.generateAccessToken(adminUser);
            String refreshToken = jwtService.generateRefreshToken(adminUser);
            assertThat(parseClaims(refreshToken).getExpiration())
                    .isAfter(parseClaims(accessToken).getExpiration());
        }
    }

    // ===================== Token Extraction =====================

    @Nested
    @DisplayName("Token Extraction Tests")
    class TokenExtractionTests {

        @Test
        @DisplayName("extractUsername - should return the correct email from access token")
        void extractUsername_AccessToken_ShouldReturnCorrectEmail() {
            String token = jwtService.generateAccessToken(teacherUser);
            assertThat(jwtService.extractUsername(token)).isEqualTo("teacher@smartcampus.com");
        }

        @Test
        @DisplayName("extractUsername - should return the correct email from refresh token")
        void extractUsername_RefreshToken_ShouldReturnCorrectEmail() {
            String refreshToken = jwtService.generateRefreshToken(studentUser);
            assertThat(jwtService.extractUsername(refreshToken)).isEqualTo("student@smartcampus.com");
        }
    }

    // ===================== Token Validation =====================

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("isTokenValid - valid access token with matching user should return true")
        void isTokenValid_ValidTokenMatchingUser_ShouldReturnTrue() {
            String token = jwtService.generateAccessToken(adminUser);
            assertThat(jwtService.isTokenValid(token, adminUserDetails)).isTrue();
        }

        @Test
        @DisplayName("isTokenValid - valid access token with different user should return false")
        void isTokenValid_ValidTokenDifferentUser_ShouldReturnFalse() {
            String token = jwtService.generateAccessToken(adminUser);
            assertThat(jwtService.isTokenValid(token, teacherUserDetails)).isFalse();
        }

        @Test
        @DisplayName("isTokenValid - expired token should return false or throw")
        void isTokenValid_ExpiredToken_ShouldThrowOrReturnFalse() throws Exception {
            setField(jwtService, "jwtExpiration", 0L);
            String token = jwtService.generateAccessToken(adminUser);
            setField(jwtService, "jwtExpiration", ACCESS_EXPIRATION);

            try {
                boolean isValid = jwtService.isTokenValid(token, adminUserDetails);
                assertThat(isValid).isFalse();
            } catch (ExpiredJwtException e) {
                assertThat(e).isNotNull();
            }
        }

        @Test
        @DisplayName("isTokenValid - refresh token with matching user should be valid")
        void isTokenValid_RefreshToken_ShouldBeValid() {
            String refreshToken = jwtService.generateRefreshToken(teacherUser);
            assertThat(jwtService.isTokenValid(refreshToken, teacherUserDetails)).isTrue();
        }

        @Test
        @DisplayName("isTokenValid - refresh token with wrong user should return false")
        void isTokenValid_RefreshTokenWrongUser_ShouldReturnFalse() {
            String refreshToken = jwtService.generateRefreshToken(adminUser);
            assertThat(jwtService.isTokenValid(refreshToken, studentUserDetails)).isFalse();
        }
    }

    // ===================== Token per Role =====================

    @Nested
    @DisplayName("Token Generation per Role Tests")
    class TokenPerRoleTests {

        @Test
        @DisplayName("Admin - token should contain ROLE_ADMIN")
        void adminUser_ShouldGenerateValidToken() {
            String token = jwtService.generateAccessToken(adminUser);
            assertThat(jwtService.extractUsername(token)).isEqualTo("admin@smartcampus.com");
            assertThat(parseClaims(token).get("role")).isEqualTo("ROLE_ADMIN");
            assertThat(jwtService.isTokenValid(token, adminUserDetails)).isTrue();
        }

        @Test
        @DisplayName("Teacher - token should contain ROLE_TEACHER")
        void teacherUser_ShouldGenerateValidToken() {
            String token = jwtService.generateAccessToken(teacherUser);
            assertThat(jwtService.extractUsername(token)).isEqualTo("teacher@smartcampus.com");
            assertThat(parseClaims(token).get("role")).isEqualTo("ROLE_TEACHER");
            assertThat(jwtService.isTokenValid(token, teacherUserDetails)).isTrue();
        }

        @Test
        @DisplayName("Student - token should contain ROLE_STUDENT")
        void studentUser_ShouldGenerateValidToken() {
            String token = jwtService.generateAccessToken(studentUser);
            assertThat(jwtService.extractUsername(token)).isEqualTo("student@smartcampus.com");
            assertThat(parseClaims(token).get("role")).isEqualTo("ROLE_STUDENT");
            assertThat(jwtService.isTokenValid(token, studentUserDetails)).isTrue();
        }
    }
}
