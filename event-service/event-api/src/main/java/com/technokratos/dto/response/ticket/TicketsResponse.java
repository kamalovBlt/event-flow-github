package com.technokratos.dto.response.ticket;

import com.technokratos.dto.GeoCoordinateDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = """
        Модель списка билетов
        """)
public record TicketsResponse (

        @Schema(description = "Название локации, где планируется провести мероприятие")
        String locationName,

        @Schema(description = "Название зала локации, где планируется провести мероприятие")
        String hallName,

        @Schema(description = "Координаты локации")
        GeoCoordinateDTO geoCoordinateDTO,

        @ArraySchema(schema = @Schema(description = "Список билетов", implementation = TicketResponse.class))
        @NotEmpty
        List<TicketResponse> tickets

) {
}

