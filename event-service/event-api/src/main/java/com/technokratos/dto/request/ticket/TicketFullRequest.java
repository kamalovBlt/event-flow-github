package com.technokratos.dto.request.ticket;

import com.technokratos.dto.TicketCategoryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = """
        Модель билета для запроса
        """)
public record TicketFullRequest(

        @Schema(description = "ID локации")
        @NotBlank
        String locationId,

        @Schema(description = "ID зала локации")
        @NotBlank
        String hallId,

        @Schema(description = "Номер ряда")
        @NotNull
        Long rowNum,

        @Schema(description = "Номер места")
        @NotNull
        Long seatNum,

        @Schema(implementation = TicketCategoryDTO.class)
        @NotNull
        @Valid
        TicketCategoryDTO category,

        @Schema(description = "Стоимость билета в рублях")
        @DecimalMin(value = "0.0", inclusive = false, message = "Стоимость должна быть больше 0")
        BigDecimal cost
) {
}
