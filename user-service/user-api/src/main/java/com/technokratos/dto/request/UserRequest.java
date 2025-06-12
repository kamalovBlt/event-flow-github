package com.technokratos.dto.request;

import com.technokratos.dto.AuthProviderDTO;
import com.technokratos.dto.RoleDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Модель пользователя для запроса")
public record UserRequest(

        @Schema(
                description = "Электронная почта пользователя (уникальная)",
                format = "email"
        )
        @Email(message = "Неправильный формат электронной почты")
        String email,

        @Schema(
                description = "Пароль пользователя, минимум 6 символов",
                minLength = 6,
                format = "password"
        )
        @NotBlank(message = "Пароль не должен быть пустым")
        @Size(min = 6, message = "Длина пароля должна быть от 6 символов")
        String password,

        @Schema(description = "Имя пользователя")
        @NotBlank(message = "Имя пользователя не может быть пустым")
        String firstName,

        @Schema(description = "Фамилия пользователя")
        @NotBlank(message = "Фамилия пользователя не может быть пустым")
        String lastName,

        @Schema(
                description = "Город пользователя",
                defaultValue = "null"
        )
        String city,

        @Schema(
                description = "boolean значение: \n если true - то профиль публичный",
                defaultValue = "false"
        )
        boolean isPublicProfile,

        @ArraySchema(schema = @Schema(implementation = RoleDTO.class))
        @NotNull(message = "Роль пользователя не может быть пустым")
        List<RoleDTO> roles,

        @Schema(implementation = AuthProviderDTO.class)
        @NotNull(message = "Вариант регистрации не может быть пустым")
        AuthProviderDTO authProvider
) {
}
