package com.technokratos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Модель места в ряду для ответа")
public record SeatResponse(
        @Schema(description = "Номер места")
        Integer num
) {}
