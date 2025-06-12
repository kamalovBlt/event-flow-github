package com.technokratos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Категория мероприятия")
public enum EventCategoryDTO {
    CONCERT, SPORT, CULTURAL
}
