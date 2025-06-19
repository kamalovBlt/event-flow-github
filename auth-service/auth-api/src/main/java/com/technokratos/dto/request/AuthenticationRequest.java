package com.technokratos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Модель запроса для аутентификации уже сохраненного пользователя")
public record AuthenticationRequest(
        @Schema(description = "Почта пользователя")
        @Email(message = "Неправильный формат электронной почты")
        @NotNull
        String email,
        @Schema(description = "Пароль пользователя")
        @NotBlank(message = "Пароль не должен быть пустым")
        @Size(min = 6, message = "Длина пароля должна быть от 6 символов")
        String password
) {
}
