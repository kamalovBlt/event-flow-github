package com.technokratos.dto.request;

import com.technokratos.dto.AddressDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.List;

@Schema(description = "Модель локации для запроса")
public record LocationRequest(

        @Schema(description = "Название локации")
        @NotBlank(message = "Название не может быть пустым")
        @Size(max = 120, message = "Название не может быть длиннее 120 символов")
        String name,

        @Schema(description = "ID пользователя")
        @NotNull
        @Positive
        Long userId,

        @Schema(description = "Описание локации")
        @Size(max = 1500, message = "Описание не может быть длиннее 1500 символов")
        String description,

        @Schema(description = "Широта локации")
        @NotNull(message = "Широта не может быть пустой")
        @Min(value = -90, message = "Широта должна быть не меньше -90")
        @Max(value = 90, message = "Широта должна быть не больше 90")
        Double latitude,

        @Schema(description = "Долгота локации")
        @NotNull(message = "Долгота не может быть пустой")
        @Min(value = -180, message = "Долгота должна быть не меньше -180")
        @Max(value = 180, message = "Долгота должна быть не больше 180")
        Double longitude,

        @Schema(implementation = AddressDto.class)
        @Valid
        AddressDto addressDto,

        @ArraySchema(schema = @Schema(implementation = HallRequest.class))
        @NotEmpty(message = "Число залов не должно быть равно 0")
        @Valid
        List<HallRequest> hall
) {
}

