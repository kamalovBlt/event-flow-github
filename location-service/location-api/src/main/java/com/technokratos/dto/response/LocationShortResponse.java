package com.technokratos.dto.response;

import com.technokratos.dto.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Модель краткой информации о локации для отображения в списке")
public record LocationShortResponse(

        @Schema(description = "Название локации")
        String name,

        @Schema(description = "Описание локации")
        String description,

        @Schema(implementation = AddressDto.class)
        AddressDto address
) {}
