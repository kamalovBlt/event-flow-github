package com.technokratos.dto.request.ticket;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = """
        Модель списка билетов для использования при создании мероприятия
        """)
public record TicketsRequest(

        @Schema(description = "ID локации")
        @NotNull
        Long locationId,

        @Schema(description = "ID зала локации")
        @NotNull
        Long hallId,

        @ArraySchema(schema = @Schema(description = "Список билетов", implementation = TicketRequest.class))
        @NotEmpty
        List<TicketRequest> tickets

) {
}
