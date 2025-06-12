package com.technokratos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Модель ряда для ответа")
public record RowResponse(
        @Schema(description = "Номер ряда")
        Integer num,

        @Schema(description = "Список мест")
        List<SeatResponse> seat
) {}
