package com.technokratos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Модель ответа для JWT токенов")
public record AuthenticationResponse(
        @Schema(description = "Access токен")
        String accessToken,
        @Schema(description = "Refresh токен")
        String refreshToken,
        @Schema(description = "Id пользователя")
        Long userId
) {
}
