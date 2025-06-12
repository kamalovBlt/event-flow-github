package com.technokratos.dto.request.sort;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Параметр сортировки с аргументом и направлением для поиска мероприятий")
public record SortParameterRequest(
        @Schema(implementation = SortArgumentRequest.class)
        @NotNull(message = "Аргумент сортировки не может быть пустым")
        SortArgumentRequest sortArgumentRequest,

        @Schema(implementation = SortDirectionRequest.class)
        @NotNull(message = "Направление сортировки не может быть пустым")
        SortDirectionRequest sortDirectionRequest
) {
}
