package com.technokratos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Модель запроса для двухфакторной аутентификации по email")
public record VerificationRequest(
        @Email
        @NotNull
        String email,
        @NotBlank
        @Size(min = 4, max = 4)
        String code
) {
}
