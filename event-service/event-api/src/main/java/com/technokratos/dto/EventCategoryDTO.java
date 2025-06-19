package com.technokratos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Категория мероприятия")
public enum EventCategoryDTO {
    NO_CATEGORY, CONCERT, SPORT, CULTURAL
}
