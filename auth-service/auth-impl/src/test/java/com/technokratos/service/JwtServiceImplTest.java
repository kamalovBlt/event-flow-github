package com.technokratos.service;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.technokratos.exception.JwtGenerationFailedException;
import com.technokratos.model.JwtToken;
import com.technokratos.model.JwtTokenWithId;
import com.technokratos.repository.impl.RedisRefreshTokenRepository;
import com.technokratos.service.impl.JwtServiceImpl;
import com.technokratos.service.properties.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {
        JwtServiceImpl.class,
        JwtProperties.class
})
@ActiveProfiles("test")
public class JwtServiceImplTest {

    @MockitoBean
    RedisRefreshTokenRepository refreshTokenRepository;

    @Autowired
    JwtServiceImpl jwtService;

    @Autowired
    JwtProperties jwtProperties;

    @Test
    void generateTokensShouldReturnValidTokens() {
        long id = 123;
        String email = "test@test.com";
        List<String> roles = List.of("USER", "ADMIN");
        JwtToken jwtToken = jwtService.generateTokens(id, email, roles);

        assertNotNull(jwtToken);
        assertNotNull(jwtToken.accessToken());
        assertNotNull(jwtToken.refreshToken());

        verify(refreshTokenRepository).save(eq(email), eq(jwtToken.refreshToken()));


    }

    @Test
    void publicKeyShouldReturnKey() {
        RSAKey publicKey = jwtService.publicKey();
        Map<String, Object> jsonObject = publicKey.toJSONObject();
        assertNotNull(publicKey);
        assertNotNull(jsonObject);
    }

    @Test
    void generateTokensShouldThrowExceptionWhenSavingFails() {
        long id = 123;
        String email = "test@test.com";
        List<String> roles = List.of("USER");

        doThrow(new RuntimeException("Redis down")).when(refreshTokenRepository).save(anyString(), anyString());

        JwtGenerationFailedException exception = assertThrows(
                JwtGenerationFailedException.class,
                () -> jwtService.generateTokens(id, email, roles)
        );

        assertEquals("JWT generation failed", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Redis down", exception.getCause().getMessage());
    }

    @Test
    void generateTokensFromRefreshTokenShouldReturnNewTokensWhenValid() throws Exception {
        long userId = 123L;
        String email = "test@test.com";
        List<String> roles = List.of("USER", "ADMIN");

        JwtToken jwtToken = jwtService.generateTokens(userId, email, roles);
        verify(refreshTokenRepository).save(eq(email), eq(jwtToken.refreshToken()));
        String refreshToken = jwtToken.refreshToken();

        when(refreshTokenRepository.findByEmail(email)).thenReturn(refreshToken);

        JwtTokenWithId newTokens = jwtService.generateTokensFromRefreshToken(refreshToken);

        assertNotNull(newTokens);
        assertNotNull(newTokens.accessToken());
        assertNotNull(newTokens.refreshToken());
        verify(refreshTokenRepository).save(eq(email), eq(newTokens.refreshToken()));
        validateTokens(userId, email, "USER ADMIN", newTokens);

    }

    void validateTokens(Long id, String email, String scope, JwtTokenWithId jwtToken) throws ParseException {
        SignedJWT accessToken = SignedJWT.parse(jwtToken.accessToken());
        assertNotNull(accessToken);
        JWTClaimsSet accessTokenJwtClaimsSet = accessToken.getJWTClaimsSet();
        assertNotNull(accessTokenJwtClaimsSet);
        Object idFromAccessToken = accessTokenJwtClaimsSet.getClaim("user-id");
        assertNotNull(idFromAccessToken);
        assertEquals(id, Long.parseLong(idFromAccessToken.toString()));
        Object rolesFromAccessToken = accessTokenJwtClaimsSet.getClaim("scope");
        assertNotNull(rolesFromAccessToken);
        assertEquals(scope, rolesFromAccessToken);

        SignedJWT refreshToken = SignedJWT.parse(jwtToken.refreshToken());
        assertNotNull(refreshToken);
        SignedJWT signedRefreshToken = SignedJWT.parse(jwtToken.refreshToken());
        assertNotNull(signedRefreshToken);
        JWTClaimsSet refreshTokenJWTClaimsSet = signedRefreshToken.getJWTClaimsSet();
        Object idFromRefreshToken = refreshTokenJWTClaimsSet.getClaim("user-id");
        assertNotNull(idFromRefreshToken);
        assertEquals(id, Long.parseLong(idFromRefreshToken.toString()));
        Object rolesFromRefreshToken = refreshTokenJWTClaimsSet.getClaim("scope");
        assertNotNull(rolesFromRefreshToken);
        assertEquals(scope, rolesFromRefreshToken);
        Object type = refreshTokenJWTClaimsSet.getClaim("type");
        assertNotNull(type);
        assertEquals("refresh", type);
        String emailFromRefreshToken = refreshTokenJWTClaimsSet.getSubject();
        assertNotNull(emailFromRefreshToken);
        assertEquals(email, emailFromRefreshToken);
    }

    @Test
    void generateAccessTokenToServicesShouldReturnValidServiceToken() throws Exception {
        String token = jwtService.generateAccessTokenToServices();
        assertNotNull(token, "Сервисный токен не должен быть null");
        SignedJWT signedJWT = SignedJWT.parse(token);
        assertNotNull(signedJWT);
        boolean verified = signedJWT.verify(new RSASSAVerifier(jwtService.publicKey()));
        assertTrue(verified, "Подпись сервисного токена должна быть валидна");
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        assertNotNull(claims);
        assertEquals("service", claims.getSubject());
        assertEquals("auth-service", claims.getIssuer());
        assertEquals("SERVICE", claims.getClaim("scope"));
        assertNotNull(claims.getIssueTime());
        assertNotNull(claims.getExpirationTime());
        assertNotNull(claims.getJWTID());
        assertTrue(claims.getExpirationTime().after(new Date()));
    }


}
