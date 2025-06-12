package com.technokratos.model;

public record JwtToken(
        String accessToken,
        String refreshToken
) {
}
