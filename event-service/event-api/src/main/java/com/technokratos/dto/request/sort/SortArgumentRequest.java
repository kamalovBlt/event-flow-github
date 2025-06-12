package com.technokratos.dto.request.sort;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Аргумент сортировки при поиске мероприятий")
public enum SortArgumentRequest {
    POPULARITY, DATE, RELEVANCE
}
