package com.technokratos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Местоположенеи")
public record GeoCoordinateDTO(
        @Schema(description = "Широта", example = "55.7558")
        Double latitude,

        @Schema(description = "Долгота", example = "37.6176")
        Double longitude
) {}
