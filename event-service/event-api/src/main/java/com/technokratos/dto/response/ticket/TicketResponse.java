package com.technokratos.dto.response.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = """
        Модель билета
        Используется в TicketsResponse (ID берутся оттуда)
        """)
public record TicketResponse(

        @Schema(description = "ID билета, используется для покупки")
        Long id,

        @Schema(description = "Номер ряда")
        Long rowNum,

        @Schema(description = "Номер места")
        Long seatNum,

        @Schema(description = "Продан ли билет", defaultValue = "false")
        Boolean isSell,

        @Schema(description = "Стоимость билета в рублях")
        BigDecimal cost

) {
}
