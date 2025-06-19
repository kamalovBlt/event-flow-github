package com.technokratos.dto.request.ticket;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = """
        Модель списка билетов для использования при создании мероприятия
        """)
public record TicketsRequest(

        @Schema(description = "ID локации")
        @NotBlank
        String locationId,

        @Schema(description = "ID зала локации")
        @NotBlank
        String hallId,

        @ArraySchema(schema = @Schema(description = "Список билетов", implementation = TicketRequest.class))
        @NotEmpty
        @Valid
        List<TicketRequest> tickets

) {
}
