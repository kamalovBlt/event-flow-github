package com.technokratos.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Модель ряда для запроса")
public record RowRequest(

        @Schema(description = "Номер ряда в зале")
        @NotNull(message = "Номер ряда не должен быть пустым")
        Integer num,

        @NotEmpty(message = "Количество мест не должно равняться 0")
        @ArraySchema(schema = @Schema(implementation = SeatRequest.class))
        @Valid
        List<SeatRequest> seat
) {
}