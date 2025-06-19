package com.technokratos.dto.request;

import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.request.ticket.TicketsRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
        description = """
        Модель мероприятия для запроса
        Прежде чем отправить запрос на создание мероприятия
        пользователь выбирает локацию и зал и получает их ID
        """
)
public record EventRequest(

        @Schema(description = "Название мероприятия")
        @NotBlank
        String name,

        @Schema(description = "Описание мероприятия")
        @NotBlank
        String description,

        @Schema(implementation = EventCategoryDTO.class)
        EventCategoryDTO category,

        @Schema(description = "ID локации, где планируется провести мероприятие")
        @NotBlank
        String locationId,

        @Schema(description = "ID зала локации, где планируется провести мероприятие")
        @NotBlank
        String hallId,

        @Schema(description = "ID пользователя, от имени которого создается")
        @NotNull
        @Positive
        Long userId,

        @Schema(description = "Время начала мероприятия", example = "2025-04-29T18:30")
        @NotNull
        LocalDateTime startTime,

        @Schema(description = "Время окончания мероприятия", example = "2025-04-29T20:30")
        @NotNull
        LocalDateTime endTime,

        @Schema(implementation = TicketsRequest.class)
        @NotNull
        @Valid
        TicketsRequest tickets,

        @Schema(description = "Список ID артистов")
        List<Long> artistIds
) {
}
