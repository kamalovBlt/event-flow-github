package com.technokratos.dto.request;

import com.technokratos.dto.RoleDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = """
        Модель пользователя для запроса, используется если
        авторизация через google вернула NOT FOUND
        """)
public record GoogleRegisterRequest(
        @Schema(
                description = "Электронная почта пользователя (уникальная)",
                format = "email"
        )
        @Email(message = "Неправильный формат электронной почты")
        @NotNull
        String email,
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
        List<RoleDTO> roles
) {
}
