package com.technokratos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Авторизационный код от google")
public record GoogleAuthorizationCodeRequest(
        @NotEmpty
        String code
) {
}
