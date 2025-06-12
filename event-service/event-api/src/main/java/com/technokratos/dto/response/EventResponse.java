package com.technokratos.dto.response;

import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.GeoCoordinateDTO;
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

        @Schema(description = "Название локации, где планируется провести мероприятие")
        String locationName,

        @Schema(description = "Название зала локации, где планируется провести мероприятие")
        String hallName,

        @Schema(description = "Координаты локации")
        GeoCoordinateDTO geoCoordinateDTO,

        @Schema(description = "Время начала мероприятия", example = "2025-04-29T18:30")
        LocalDateTime startTime,

        @Schema(description = "Время окончания мероприятия", example = "2025-04-29T20:30")
        LocalDateTime endTime,

        @ArraySchema(schema = @Schema(description = "Список из ID изображений"))
        List<Long> imagesId
) {
}
