package com.technokratos.api;

import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.request.sort.SortParameterRequest;
import com.technokratos.dto.response.EventResponse;
import com.technokratos.dto.response.EventServiceErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/v1/event-service/events")
@Tag(
        name = "Мероприятия",
        description = "Операции с мероприятиями"
)
public interface EventApi {

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Поиск мероприятий",
            description = """
                    Возвращает список найденных мероприятий на
                    основе выбранных параметров поиска.
                    Поддерживает пагинацию.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список мероприятий успешно получен",
                            content = @Content(schema = @Schema(implementation = EventResponse.class, type = "array"))
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
    List<EventResponse> find(

            @RequestParam(value = "sortParam", required = false)
            @Parameter(description = "Параметры сортировки")
            @Schema(implementation = SortParameterRequest.class)
            SortParameterRequest sortParameter,

            @RequestParam(value = "dateStart", required = false)
            @Parameter(description = "Фильтр по дате проведения начало включительно")
            LocalDateTime date1,

            @RequestParam(value = "dateEnd", required = false)
            @Parameter(description = "Фильтр по дате проведения конец включительно")
            LocalDateTime date2,

            @RequestParam(value = "categories", required = false)
            @Parameter(description = "Фильтр по категориям мероприятий")
            List<EventCategoryDTO> categories,

            @RequestParam(value = "keywords", required = false)
            @Parameter(description = "Ключевые слова для поиска в названии или описании мероприятия")
            String keywords
    );

    @GetMapping("/{event-id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получение мероприятия по ID",
            description = "Возвращает информацию о мероприятии по указанному ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Мероприятие успешно найдено",
                            content = @Content(schema = @Schema(implementation = EventResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Мероприятие с указанным ID не найдено",
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
    EventResponse findById(
            @PathVariable("event-id")
            @Parameter(description = "ID мероприятия", required = true)
            Long eventId
    );

    @GetMapping("/recommendations")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получение рекомендаций мероприятий",
            description = """
                    Возвращает список рекомендованных мероприятий на
                    основе предпочтений пользователя.
                    Поддерживает пагинацию.
                    Доступ: USER, PLATFORM, ORGANIZER, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список рекомендованных мероприятий успешно получен",
                            content = @Content(schema = @Schema(implementation = EventResponse.class, type = "array"))
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
    List<EventResponse> getRecommendations(
            @Parameter(description = "ID пользователя")
            @RequestParam("user-id") Long userId,

            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы (количество мероприятий на странице)")
            @RequestParam(defaultValue = "10") int size
    );

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Сохрание мероприятия",
            description = """
                    Принимает объект EventRequest, возвращает id сохраненного мероприятия.
                    Не сохраняет изображения и видео привязанные к мероприятию,
                    для сохранения изображения используйте методы saveImages и saveVideo
                    Доступ: ORGANIZER, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Мероприятие успешно сохранено",
                            content = @Content(schema = @Schema(implementation = Long.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав для создания мероприятия",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = """
                                    Зал локации занят в данное время,
                                    в ответе указывается id мероприятия,
                                    которое проводится в это времяя
                                    """,
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    Long save(EventRequest eventRequest);

    @PutMapping("/{event-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Обновление мероприятия",
            description = """
                    Принимает ID мероприятия объект EventRequest.
                    Не обновляет изображения и видео привязанные к мероприятию,
                    для обновления изображений и виедо используйте методы updateImages и updateVideo
                    Доступ: ORGANIZER если создавал, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Мероприятие успешно обновлено"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав для обновления мероприятия",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = """
                                    Зал локации занят в данное время,
                                    в ответе указывается id мероприятия,
                                    которое проводится в это времяя
                                    """,
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void update(
            @PathVariable("event-id")
            @Parameter(description = "ID меропрития", required = true)
            Long eventId,
            EventRequest eventRequest
    );

    @DeleteMapping("/{event-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление мероприятия",
            description = """
                    Принимает ID мероприятия объект EventRequest.
                    Удаляет все, что связано с мероприятием.
                    Доступ: ORGANIZER если создавал, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Мероприятие успешно удалено"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав для удаление мероприятия",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),

                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void delete(
            @PathVariable("event-id")
            @Parameter(description = "ID меропрития", required = true)
            Long eventId
    );


}
