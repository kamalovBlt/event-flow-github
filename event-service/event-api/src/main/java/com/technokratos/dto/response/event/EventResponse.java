package com.technokratos.dto.response.event;

import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.response.artist.ArtistShortResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Модель мероприятия для ответа, без билетов")
public record EventResponse(
        @Schema(description = "Название мероприятия")
        String name,

        @Schema(description = "Описание мероприятия")
        String description,

        @Schema(implementation = EventCategoryDTO.class)
        EventCategoryDTO category,

        @Schema(description = "ID локации, где планируется провести мероприятие", implementation = String.class)
        String locationId,

        @Schema(description = "ID зала локации, где планируется провести мероприятие")
        String hallId,

        @Schema(description = "Время начала мероприятия", example = "2025-04-29T18:30")
        LocalDateTime startTime,

        @Schema(description = "Время окончания мероприятия", example = "2025-04-29T20:30")
        LocalDateTime endTime,

        @ArraySchema(schema = @Schema(description = "Список артистов(краткая информация) участвующих в данном мероприятии", implementation = ArtistShortResponse.class))
        List<ArtistShortResponse> artists,

        @ArraySchema(schema = @Schema(description = "Список из ID изображений"))
        List<Long> imageIds
) {
}
