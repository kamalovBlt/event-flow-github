package com.technokratos.dto.response.ticket;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = """
        Модель списка билетов
        """)
public record TicketsResponse (

        @Schema(description = "ID локации, где планируется провести мероприятие")
        String locationId,

        @Schema(description = "ID зала локации, где планируется провести мероприятие")
        String hallId,

        @ArraySchema(schema = @Schema(description = "Список билетов", implementation = TicketResponse.class))
        @NotEmpty
        List<TicketResponse> tickets

) {
}

