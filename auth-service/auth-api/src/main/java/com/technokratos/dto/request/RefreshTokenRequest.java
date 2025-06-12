package com.technokratos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Refresh токен")
public record RefreshTokenRequest(
        @NotEmpty
        String refreshToken
) {

}
