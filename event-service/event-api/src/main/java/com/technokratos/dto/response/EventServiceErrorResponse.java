package com.technokratos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель сообщения, описывающее ошибку с мероприятиями")
public class EventServiceErrorResponse {

    @Schema(description = "Описание ошибки", nullable = true)
    private String message;
    @Schema(description = "ID мероприятия, при работе с которым произошла ошибка", nullable = true)
    private String eventId;
    @Schema(description = "Название исключения", nullable = true)
    private String exceptionName;

}
