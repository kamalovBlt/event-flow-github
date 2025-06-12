package com.technokratos.dto.response;

import com.technokratos.dto.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Модель локации для ответа")
public record LocationResponse(

        @Schema(description = "Название локации")
        String name,

        @Schema(description = "Описание локации")
        String description,

        @Schema(implementation = AddressDto.class)
        AddressDto address,

        @Schema(description = "Список залов")
        List<HallResponse> hall
) {}
