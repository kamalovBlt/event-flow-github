package com.technokratos.dto.request;


import com.technokratos.dto.AuthProviderDTO;
import com.technokratos.dto.RoleDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserWithOAuthRequest(

        @Email(message = "Неправильный формат электронной почты")
        String email,

        @NotBlank(message = "Имя пользователя не может быть пустым")
        String firstName,

        @NotBlank(message = "Фамилия пользователя не может быть пустым")
        String lastName,

        String city,

        boolean isPublicProfile,

        @ArraySchema(schema = @Schema(implementation = RoleDTO.class))
        @NotNull(message = "Роль пользователя не может быть пустым")
        List<RoleDTO> role,

        @NotNull(message = "Вариант регистрации не может быть пустым")
        @Schema(implementation = AuthProviderDTO.class)
        AuthProviderDTO authProvider
) {

}
