package com.technokratos.dto.response.artist;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с краткой информацией об артисте.")
public record ArtistShortResponse(

        @Schema(description = "Id артиста")
        Long id,

        @Schema(description = "Имя артиста", example = "Иван")
        String firstName,

        @Schema(description = "Фамилия артиста", example = "Иванов")
        String lastName,

        @Schema(description = "Псевдоним артиста", example = "DJ Vanya")
        String nickname

) {
}
