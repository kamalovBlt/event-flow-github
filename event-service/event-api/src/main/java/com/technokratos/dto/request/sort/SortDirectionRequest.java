package com.technokratos.dto.request.sort;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Направление сортировки при поиске мероприятий")
public enum SortDirectionRequest {
    ASC, DESC
}
