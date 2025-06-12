package com.technokratos.dto.response;

import com.technokratos.dto.RoleDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Модель пользователя для ответа")
public record UserResponse(

        @Schema(
                description = "Электронная почта пользователя (уникальная)",
                format = "email"
        )
        String email,

        @Schema(
                description = "Имя пользователя"
        )
        String firstName,

        @Schema(
                description = "Фамилия пользователя"
        )
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
        List<RoleDTO> roles
) {
}
