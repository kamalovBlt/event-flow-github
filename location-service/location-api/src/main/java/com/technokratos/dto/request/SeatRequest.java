package com.technokratos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Модель места в ряду для запроса")
public record SeatRequest(
        @Schema(description = "Номер места в ряду")
        @NotNull(message = "Номер места в ряду не должен быть пустым")
        Integer num
        ) {}