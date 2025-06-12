package com.technokratos.dto.request.ticket;

import com.technokratos.dto.TicketCategoryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = """
        Модель билета для использования при создании мероприятия
        Используется в TicketsRequest (ID берутся оттуда)
        """)
public record TicketRequest(

        @Schema(description = "Номер ряда")
        @NotNull
        Long rowNum,

        @Schema(description = "Номер места")
        @NotNull
        Long seatNum,

        @Schema(implementation = TicketCategoryDTO.class)
        @NotNull
        TicketCategoryDTO category,

        @Schema(description = "Стоимость билета в рублях")
        int cost

) {
}
