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

public record UserWithOAuthRequest(

        @Email(message = "Неправильный формат электронной почты")
        @NotBlank(message = "Электронная почта не может быть пустым")
        @Size(max = 320, message = "Почта не может быть длиннее 320 символов")
        String email,

        @NotBlank(message = "Имя пользователя не может быть пустым")
        @Size(max = 150, message = "Имя не может быть длиннее 150 символов")
        String firstName,

        @NotBlank(message = "Фамилия пользователя не может быть пустым")
        @Size(max = 150, message = "Фамилия не может быть длиннее 150 символов")
        String lastName,

        @Size(max = 100, message = "Город не может быть длиннее 100 символов")
        String city,

        boolean isPublicProfile,

        @ArraySchema(schema = @Schema(implementation = RoleDTO.class))
        @NotNull(message = "Роль пользователя не может быть пустым")
        List<RoleDTO> roles,

        @NotNull(message = "Вариант регистрации не может быть пустым")
        @Schema(implementation = AuthProviderDTO.class)
        AuthProviderDTO authProvider
) {

}
