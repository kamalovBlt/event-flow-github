package com.technokratos.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.technokratos.exception.JwtGenerationFailedException;
import com.technokratos.exception.RefreshTokenNotValidException;
import com.technokratos.model.JwtToken;
import com.technokratos.repository.api.RefreshTokenRepository;
import com.technokratos.service.api.JwtService;
import com.technokratos.service.properties.JwtProperties;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.*;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;
    private final RSAKey privateKey;
    private final RSAKey publicKey;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtServiceImpl(JwtProperties jwtProperties, RefreshTokenRepository refreshTokenRepository) throws Exception {
        this.jwtProperties = jwtProperties;
        this.publicKey = toPublicRsaKey(jwtProperties.getPublicSecretKey());
        this.privateKey = toPrivateRsaKey(jwtProperties.getPrivateSecretKey());
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Метод генерирует access и refresh токены с использованием RSA.
     */
    @Override
    public JwtToken generateTokens(long id, String email, List<String> roles) {
        Date now = new Date();
        Date accessExpiry = new Date(now.getTime() + jwtProperties.getAccessTokenValidity());
        Date refreshExpiry = new Date(now.getTime() + jwtProperties.getRefreshTokenValidity());

        try {
            JWTClaimsSet accessClaims = new JWTClaimsSet.Builder()
                    .subject(email)
                    .issueTime(now)
                    .expirationTime(accessExpiry)
                    .issuer("auth-service")
                    .claim("user-id", id)
                    .claim("scope", String.join(" ", roles))
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            JWTClaimsSet refreshClaims = new JWTClaimsSet.Builder()
                    .subject(email)
                    .issueTime(now)
                    .expirationTime(refreshExpiry)
                    .issuer("auth-service")
                    .claim("type", "refresh")
                    .claim("user-id", id)
                    .claim("scope", String.join(" ", roles))
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            JWSSigner signer = new RSASSASigner(privateKey.toPrivateKey());

            SignedJWT signedAccessJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(privateKey.getKeyID())
                            .type(JOSEObjectType.JWT)
                            .build(),
                    accessClaims
            );
            signedAccessJWT.sign(signer);

            SignedJWT signedRefreshJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(privateKey.getKeyID())
                            .type(JOSEObjectType.JWT)
                            .build(),
                    refreshClaims
            );
            signedRefreshJWT.sign(signer);
            refreshTokenRepository.save(email, signedRefreshJWT.serialize());
            return new JwtToken(signedAccessJWT.serialize(), signedRefreshJWT.serialize());
        } catch (Exception e) {
            throw new JwtGenerationFailedException("JWT generation failed", e);
        }
    }

    @Override
    public JwtToken generateTokensFromRefreshToken(String refreshToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);
            signedJWT.verify(new RSASSAVerifier(privateKey));

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime.before(new Date())) {
                throw new RefreshTokenNotValidException("Токен недействителен");
            }

            String email = signedJWT.getJWTClaimsSet().getSubject();

            String storedToken = refreshTokenRepository.findByEmail(email);
            if (storedToken == null || !storedToken.equals(refreshToken)) {
                throw new RefreshTokenNotValidException("Токен не найден");
            }

            Long userId = (Long) signedJWT.getJWTClaimsSet().getClaim("user-id");
            String scope = (String) signedJWT.getJWTClaimsSet().getClaim("scope");
            List<String> roles = Arrays.stream(scope.split(" ")).toList();
            return generateTokens(userId, email, roles);
        } catch (ParseException e) {
            throw new RefreshTokenNotValidException("Ошибка при парсинге токена");
        } catch (JOSEException e) {
            throw new RefreshTokenNotValidException("Неправильный токен");
        }
    }

    @Override
    public RSAKey publicKey() {
        return this.publicKey;
    }

    @Override
    public String generateAccessTokenToServices() {
        Date now = new Date();
        Date accessExpiry = new Date(now.getTime() + jwtProperties.getAccessTokenValidity());
        JWTClaimsSet accessClaims = new JWTClaimsSet.Builder()
                .subject("service")
                .issueTime(now)
                .expirationTime(accessExpiry)
                .issuer("auth-service")
                .claim("scope", "SERVICE")
                .jwtID(UUID.randomUUID().toString())
                .build();
        try {
            JWSSigner signer = new RSASSASigner(privateKey.toPrivateKey());
            SignedJWT signedAccessJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(privateKey.getKeyID())
                            .type(JOSEObjectType.JWT)
                            .build(),
                    accessClaims
            );
            signedAccessJWT.sign(signer);
            return signedAccessJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private RSAKey toPrivateRsaKey(String privateKeyPem) throws Exception {
        String cleanedKey = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(cleanedKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();
    }

    private RSAKey toPublicRsaKey(String publicKeyPem) throws Exception {
        String cleanedKey = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(cleanedKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

        return new RSAKey.Builder(publicKey)
                .build();
    }

}