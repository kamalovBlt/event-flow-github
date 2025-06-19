package com.technokratos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(
        description = """
                Модель артиста для запроса.
                Используется для создания или обновления информации об артисте.
                При необходимости можно указать список ID мероприятий, в которых участвует артист.
                """
)
public record ArtistRequest(

        @Schema(description = "Имя артиста")
        @NotBlank
        @Size(max = 120, message = "Имя артиста должно быть меньше 120 символов")
        String firstName,

        @Schema(description = "Фамилия артиста")
        @NotBlank
        @Size(max = 120, message = "Фамилия артиста должна быть меньше 120 символов")
        String lastName,

        @Schema(description = "Псевдоним артиста (если есть)")
        @Size(max = 120, message = "Псевдоним артиста должен быть меньше 120 символов")
        String nickname,

        @Schema(description = "Описание или биография артиста")
        @NotBlank
        @Size(max = 1500, message = "Описание артиста должно быть меньше 1500 символов")
        String description,

        @Schema(description = "ID пользователя, от имени которого создается")
        @NotNull
        @Positive
        Long userId,

        @Schema(description = "Список ID мероприятий, в которых участвует артист")
        List<Long> eventIds
) {
}
