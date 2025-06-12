package com.technokratos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель сообщения, описывающее ошибку с пользователем")
public class UserServiceErrorResponse {

    @Schema(description = "Описание ошибки", nullable = true)
    private String message;
    @Schema(description = "ID пользователя, при работе с которым произошла ошибка", nullable = true)
    private String userId;
    @Schema(description = "Название исключения", nullable = true)
    private String exceptionName;

}
