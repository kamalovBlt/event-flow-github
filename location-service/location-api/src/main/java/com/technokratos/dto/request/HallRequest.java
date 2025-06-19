package com.technokratos.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Модель зала для запроса")
public record HallRequest(

            @Schema(description = "Название зала")
            @NotBlank(message = "Название зала не должно быть пустым")
            @Size(max = 120, message = "Название зала не может быть длиннее 120 символов")
            String name,

            @ArraySchema(schema = @Schema(implementation = RowRequest.class))
            @NotEmpty(message = "Число рядов не должно быть равным 0")
            @Valid
            List<RowRequest> row
        ) {}