package com.technokratos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Информация об адресе локации")
public record AddressDto(
    @Schema(description = "Название страны")
    @NotBlank(message = "Название страны не может быть пустым")
    @Size(max = 100, message = "Название страны должно быть меньше 100 символов")
    String country,

    @Schema(description = "Название города")
    @NotBlank(message = "Название города не может быть пустым")
    @Size(max = 100, message = "Название города должно быть меньше 100 символов")
    String city,

    @Schema(description = "Название улицы")
    @NotBlank(message = "Название улицы не может быть пустым")
    @Size(max = 200, message = "Название улицы должно быть меньше 20 символов")
    String street,

    @Schema(description = "Номер дома")
    @NotBlank(message = "Название дома не может быть пустым")
    @Size(max = 20, message = "Номер дома должен быть меньше 20 символов")
    String building
) {}