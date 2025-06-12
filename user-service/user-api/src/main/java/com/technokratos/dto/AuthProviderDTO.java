package com.technokratos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Модель вариантов регистрации в систему пользователем.
        Вариант регистрации:
            LOCAL - обычный регистрация
            GOOGLE - регистрация через google аккаунт
        """)
public enum AuthProviderDTO {
    LOCAL,GOOGLE
}
