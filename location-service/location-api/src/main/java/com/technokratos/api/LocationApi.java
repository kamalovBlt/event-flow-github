package com.technokratos.api;

import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.response.LocationResponse;
import com.technokratos.dto.response.LocationServiceErrorResponse;
import com.technokratos.dto.response.LocationShortResponse;
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

@RequestMapping("/api/v1/location-service/locations")
@Tag(
        name = "Локации",
        description = "Операции с локациями для проведения мероприятий"
)
public interface LocationApi {

    @GetMapping("/{location-id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получения локации по ID",
            description = """
                    Параметр - id локации типа string,
                    указывается в URI, возвращает все данные о локации
                    Параметр - id локации типа long,
                    указывается в URI, возвращает все данные о локации.
                    Доступ: ORGANIZER, PLATFORM, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Локация найдена",
                            content = @Content(schema = @Schema(implementation = LocationResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неправильный формат ввода ID",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))

                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Локация не найдена",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))

                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, возвращает название исключения",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))

                    )

            }
    )
    LocationResponse findById(
            @Parameter(
                    description = "ID локации (число), указывается в URI",
                    required = true
            )
            @PathVariable("location-id") String id
    );

    @GetMapping("/recommendations")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получение рекомендованных локаций",
            description = """
                    Возвращает список рекомендованных локаций.
                    Поддерживает параметры пагинации: page (номер страницы), size (размер страницы).
                    Доступ: ORGANIZER, PLATFORM, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Рекомендации локаций успешно получены",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = LocationShortResponse.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные параметры запроса (page или size)",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Локации не найдены",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, возвращает название исключения",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))
                    )
            }
    )
    List<LocationShortResponse> getRecommendedLocations(
            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы (количество локаций на странице)")
            @RequestParam(defaultValue = "10") int size
    );

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Сохранение локации",
            description = """
                    Тело запроса - объект LocationRequest,
                    возвращает id (long) сохраненной локации
                    Доступ: PLATFORM, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Локация успешно сохранена, тело ответа содержит id",
                            content = @Content(schema = @Schema(implementation = Long.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, возвращает название исключения",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))

                    )

            }

    )
    String save(@Valid
              @RequestBody
              LocationRequest locationRequest);

    @PutMapping("/{location-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Обновление локации",
            description = """
                    Параметр - ID, тело запроса -
                    полный объект LocationRequest, обновляет локацию
                    Доступ: PLATFORM если создавал данную, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Локация успешно обновлена"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Локация с таким ID не найдена",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутрення ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))
                    )
            }
    )
    void update(
            @Parameter(
                    description = "ID локации (число), которую требуется обновить, указывается в URI",
                    required = true
            )
            @PathVariable("location-id") String id,
            @Valid
            @RequestBody
            LocationRequest locationRequest);


    @DeleteMapping("/{location-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление локации",
            description = "Доступ: PLATFORM если создавал данную, ADMIN",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Локация успешно удалена"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Локация с таким ID не найдена",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутрення ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = LocationServiceErrorResponse.class))
                    )
            }
    )
    void delete(
            @Parameter(
                    description = "ID локации (число), которую следует удалить, указывается в URI",
                    required = true
            )
            @PathVariable("location-id") String id);

}
