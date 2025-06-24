package com.technokratos.service.api;

import com.nimbusds.jose.jwk.RSAKey;
import com.technokratos.model.JwtToken;
import com.technokratos.model.JwtTokenWithId;

import java.util.List;

public interface JwtService {
    JwtToken generateTokens(long id, String email, List<String> roles);
    JwtTokenWithId generateTokensFromRefreshToken(String refreshToken);
    RSAKey publicKey();
    String generateAccessTokenToServices();
}
