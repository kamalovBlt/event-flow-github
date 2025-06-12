package com.technokratos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Модель зала для ответа")
public record HallResponse(
        @Schema(description = "Название зала")
        String name,

        @Schema(description = "Список рядов")
        List<RowResponse> row
) {}
