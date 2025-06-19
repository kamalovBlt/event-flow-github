package com.technokratos.api;

import com.technokratos.dto.request.ArtistRequest;
import com.technokratos.dto.response.artist.ArtistResponse;
import com.technokratos.dto.response.EventServiceErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/event-service/artists")
@Tag(
        name = "Мероприятия",
        description = "Операции с артистами"
)
public interface ArtistApi {

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Поиск артистов",
            description = """
                    Возвращает список найденных мероприятий на основе ключевых слов.
                    Поддерживает пагинацию.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список артистов успешно получен",
                            content = @Content(schema = @Schema(implementation = ArtistResponse.class, type = "array"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    List<ArtistResponse> find(
            @Parameter(description = "Ключевые слова для поиска в: firstName, lastName, username") String keywords,
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы (с нуля)") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Размер страницы") int size
    );

    @GetMapping("/{artist-id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получение артиcта по ID",
            description = "Возвращает информацию об артисте по указанному ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Артист успешно найден",
                            content = @Content(schema = @Schema(implementation = ArtistResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Артист с указанным ID не найден",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(
                                    implementation = EventServiceErrorResponse.class
                            ))
                    )
            }
    )
    ArtistResponse findById(@PathVariable("artist-id") @Parameter(description = "ID артиста") Long artistId);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получение артиcтов с пагинацией",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Артисты успешны найден",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = List.class)))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(
                                    implementation = EventServiceErrorResponse.class
                            ))
                    )
            }
    )
    List<ArtistResponse> findAll(
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы (с нуля)") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Размер страницы") int size
    );

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Сохрание артиста",
            description = """
                    Принимает объект ArtistRequest, возвращает id сохраненного артиста.
                    Доступ: ORGANIZER, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Артист успешно сохранен",
                            content = @Content(schema = @Schema(implementation = Long.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав для создания артиста",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найдены мероприятия",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    Long save(@Valid @RequestBody ArtistRequest artistRequest);

    @PutMapping("/{artist-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Обновление артиста",
            description = """
                    Принимает ID артиста и объект ArtistRequest.
                    Доступ: ORGANIZER если создавал, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Артист успешно обновлен"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав для обновления артиста",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void update(@PathVariable("artist-id") @Parameter(description = "ID артиста") Long artistId,
                @Valid @RequestBody ArtistRequest artistRequest);

    @DeleteMapping("/{artist-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление мероприятия",
            description = """
                    Принимает ID мероприятия.
                    Удаляет все, что связано с артистом.
                    Доступ: ORGANIZER если создавал, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Артист успешно удален"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав для удаления артиста",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),

                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void delete(@PathVariable("artist-id") @Parameter(description = "ID артиста") Long artistId);

}
