package org.example.smartcompus.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET_KEY = "dGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JKV1RUb2tlbkdlbmVyYXRpb24xMjM0NTY3ODk=";
    private static final long ACCESS_EXPIRATION = 900000; // 15 minutes
    private static final long REFRESH_EXPIRATION = 604800000; // 7 days

    private UserDetails adminUser;
    private UserDetails teacherUser;
    private UserDetails studentUser;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Use reflection to set @Value fields
        setField(jwtService, "secretKey", SECRET_KEY);
        setField(jwtService, "jwtExpiration", ACCESS_EXPIRATION);
        setField(jwtService, "refreshExpiration", REFRESH_EXPIRATION);

        adminUser = new User(
                "admin@smartcampus.com", "encodedPassword",
                true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        teacherUser = new User(
                "teacher@smartcampus.com", "encodedPassword",
                true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"))
        );

        studentUser = new User(
                "student@smartcampus.com", "encodedPassword",
                true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );
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

    // ===================== Token Generation =====================

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {

        @Test
        @DisplayName("generateToken - should return a non-null, non-empty JWT string")
        void generateToken_ShouldReturnNonEmptyString() {
            String token = jwtService.generateToken(adminUser);

            assertThat(token).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("generateToken - token should contain the correct subject (email)")
        void generateToken_ShouldContainCorrectSubject() {
            String token = jwtService.generateToken(adminUser);

            String subject = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

            assertThat(subject).isEqualTo("admin@smartcampus.com");
        }

        @Test
        @DisplayName("generateToken - token should have a future expiration date")
        void generateToken_ShouldHaveFutureExpiration() {
            String token = jwtService.generateToken(adminUser);

            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            assertThat(expiration).isAfter(new Date());
        }

        @Test
        @DisplayName("generateToken - different users should produce different tokens")
        void generateToken_DifferentUsers_ShouldProduceDifferentTokens() {
            String adminToken = jwtService.generateToken(adminUser);
            String teacherToken = jwtService.generateToken(teacherUser);

            assertThat(adminToken).isNotEqualTo(teacherToken);
        }

        @Test
        @DisplayName("generateRefreshToken - should return a valid refresh token")
        void generateRefreshToken_ShouldReturnValidToken() {
            String refreshToken = jwtService.generateRefreshToken(adminUser);

            assertThat(refreshToken).isNotNull().isNotEmpty();

            String subject = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload()
                    .getSubject();

            assertThat(subject).isEqualTo("admin@smartcampus.com");
        }

        @Test
        @DisplayName("generateRefreshToken - should have longer expiration than access token")
        void generateRefreshToken_ShouldHaveLongerExpiration() {
            String accessToken = jwtService.generateToken(adminUser);
            String refreshToken = jwtService.generateRefreshToken(adminUser);

            Date accessExp = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload()
                    .getExpiration();

            Date refreshExp = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload()
                    .getExpiration();

            assertThat(refreshExp).isAfter(accessExp);
        }
    }

    // ===================== Token Extraction =====================

    @Nested
    @DisplayName("Token Extraction Tests")
    class TokenExtractionTests {

        @Test
        @DisplayName("extractUsername - should return the correct email from token")
        void extractUsername_ShouldReturnCorrectEmail() {
            String token = jwtService.generateToken(teacherUser);

            String username = jwtService.extractUsername(token);

            assertThat(username).isEqualTo("teacher@smartcampus.com");
        }

        @Test
        @DisplayName("extractUsername - should work for student tokens")
        void extractUsername_StudentToken_ShouldReturnCorrectEmail() {
            String token = jwtService.generateToken(studentUser);

            String username = jwtService.extractUsername(token);

            assertThat(username).isEqualTo("student@smartcampus.com");
        }

        @Test
        @DisplayName("extractUsername - should work for refresh tokens")
        void extractUsername_RefreshToken_ShouldReturnCorrectEmail() {
            String refreshToken = jwtService.generateRefreshToken(adminUser);

            String username = jwtService.extractUsername(refreshToken);

            assertThat(username).isEqualTo("admin@smartcampus.com");
        }
    }

    // ===================== Token Validation =====================

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("isTokenValid - valid token with matching user should return true")
        void isTokenValid_ValidTokenMatchingUser_ShouldReturnTrue() {
            String token = jwtService.generateToken(adminUser);

            boolean isValid = jwtService.isTokenValid(token, adminUser);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("isTokenValid - valid token with different user should return false")
        void isTokenValid_ValidTokenDifferentUser_ShouldReturnFalse() {
            String token = jwtService.generateToken(adminUser);

            boolean isValid = jwtService.isTokenValid(token, teacherUser);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("isTokenValid - expired token should return false")
        void isTokenValid_ExpiredToken_ShouldThrowOrReturnFalse() throws Exception {
            // Temporarily set expiration to 0 to create an expired token
            setField(jwtService, "jwtExpiration", 0L);
            String token = jwtService.generateToken(adminUser);

            // Reset expiration
            setField(jwtService, "jwtExpiration", ACCESS_EXPIRATION);

            // An expired token should either return false or throw ExpiredJwtException
            try {
                boolean isValid = jwtService.isTokenValid(token, adminUser);
                assertThat(isValid).isFalse();
            } catch (ExpiredJwtException e) {
                // Expected behavior - expired token throws exception
                assertThat(e).isNotNull();
            }
        }

        @Test
        @DisplayName("isTokenValid - refresh token should also be validatable")
        void isTokenValid_RefreshToken_ShouldBeValid() {
            String refreshToken = jwtService.generateRefreshToken(teacherUser);

            boolean isValid = jwtService.isTokenValid(refreshToken, teacherUser);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("isTokenValid - refresh token with wrong user should return false")
        void isTokenValid_RefreshTokenWrongUser_ShouldReturnFalse() {
            String refreshToken = jwtService.generateRefreshToken(adminUser);

            boolean isValid = jwtService.isTokenValid(refreshToken, studentUser);

            assertThat(isValid).isFalse();
        }
    }

    // ===================== Token for Each Role =====================

    @Nested
    @DisplayName("Token Generation per Role Tests")
    class TokenPerRoleTests {

        @Test
        @DisplayName("Admin user - should generate valid token")
        void adminUser_ShouldGenerateValidToken() {
            String token = jwtService.generateToken(adminUser);

            assertThat(jwtService.extractUsername(token)).isEqualTo("admin@smartcampus.com");
            assertThat(jwtService.isTokenValid(token, adminUser)).isTrue();
        }

        @Test
        @DisplayName("Teacher user - should generate valid token")
        void teacherUser_ShouldGenerateValidToken() {
            String token = jwtService.generateToken(teacherUser);

            assertThat(jwtService.extractUsername(token)).isEqualTo("teacher@smartcampus.com");
            assertThat(jwtService.isTokenValid(token, teacherUser)).isTrue();
        }

        @Test
        @DisplayName("Student user - should generate valid token")
        void studentUser_ShouldGenerateValidToken() {
            String token = jwtService.generateToken(studentUser);

            assertThat(jwtService.extractUsername(token)).isEqualTo("student@smartcampus.com");
            assertThat(jwtService.isTokenValid(token, studentUser)).isTrue();
        }
    }
}

