package com.technokratos.dto.response.artist;

import com.technokratos.dto.response.event.EventShortResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        description = """
                Ответ с информацией об артисте.
                Содержит основные сведения об артисте и список мероприятий, в которых он участвует.
                """
)
public record ArtistResponse(

        @Schema(description = "Имя артиста", example = "Иван")
        String firstName,

        @Schema(description = "Фамилия артиста", example = "Иванов")
        String lastName,

        @Schema(description = "Псевдоним артиста", example = "DJ Vanya")
        String nickname,

        @Schema(description = "Описание или биография артиста", example = "Опытный музыкант, выступающий на фестивалях с 2010 года.")
        String description,

        @ArraySchema(schema = @Schema(description = "Список с краткой информацией мероприятий", implementation = EventShortResponse.class))
        List<EventShortResponse> events
) {
}
