package com.technokratos.dto.response.event;

import com.technokratos.dto.EventCategoryDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Модель мероприятия для ответа с краткой информацией")
public record EventShortResponse(
        @Schema(description = "ID мероприятия, можно использовать для получения изображений")
        Long eventId,
        @Schema(description = "Название мероприятия")
        String name,
        @Schema(implementation = EventCategoryDTO.class)
        EventCategoryDTO category
) {
}
