package com.technokratos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель сообщения, описывающее ошибку с локацией")
public class LocationServiceErrorResponse {

    @Schema(description = "Описание ошибки", nullable = true)
    private String message;
    @Schema(description = "ID локации, при работе с которой произошла ошибка", nullable = true)
    private String locationId;
    @Schema(description = "Название исключения", nullable = true)
    private String exceptionName;

}