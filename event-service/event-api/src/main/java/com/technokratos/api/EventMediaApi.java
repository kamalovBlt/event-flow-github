package com.technokratos.api;

import com.technokratos.dto.response.EventServiceErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/event-service/events")
@Tag(
        name = "Мероприятия",
        description = "Операции с медиафайлами мероприятий"
)
public interface EventMediaApi {

    @GetMapping("/{event-id}/images/{image-id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получение изображения мероприятия",
            description = """
                    По ID события возвращает изображение.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Изображение успешно получено",
                            content = @Content(schema = @Schema(implementation = Resource.class, type = "array"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Мероприятие/изображение с таким ID не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    Resource findImage(
            @Parameter @PathVariable("event-id") Long eventId,
            @Parameter @PathVariable("image-id") Long imageId
    );

    @PostMapping(value = "/{event-id}/images", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Сохранение изображения мероприятия",
            description = """
                    По ID события сохраняет одно изображение как MultipartFile.
                    Максимальный размер — 1 МБ.
                    Максимум изображений - 5.
                    Доступ: ORGANIZER если создавал, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Изображение успешно сохранено, возращает ID",
                            content = @Content(schema = @Schema(implementation = Long.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав для сохранения изображения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Превышено макс. количество изображений",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    Long saveImage(
            @Parameter(description = "ID мероприятия")
            @PathVariable("event-id") Long eventId,

            @Parameter(description = "Одно изображение до 1 МБ")
            @RequestParam("image") MultipartFile image
    );

    @GetMapping("/{event-id}/video")
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @Operation(
            summary = "Получение видео мероприятия",
            description = """
                    По ID события и заголовку Range возвращает часть видео
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "206",
                            description = "Часть видео успешно получено",
                            content = @Content(schema = @Schema(implementation = Resource.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Мероприятие с таким ID не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    Resource findVideoPart(
            @Parameter @PathVariable("event-id") Long eventId,

            @Parameter(
                    description = "Заголовок Range",
                    name = "Range",
                    example = "bytes=0-1023"
            )
            @RequestHeader(value = "Range", required = false) String range
    );

    @PostMapping(value = "/{event-id}/video", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Сохранение видео мероприятия",
            description = """
                    По ID события сохраняет видео как MultipartFile
                    Максимальный размер - 10 МБ.
                    Доступ: ORGANIZER если создавал, ADMIN
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Видео успешно сохранено"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав для сохранение видео",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void saveVideo(
            @Parameter(description = "ID мероприятия")
            @PathVariable("event-id") Long eventId,

            @Parameter(description = "Видео до 10 МБ")
            @RequestParam("video") MultipartFile video
    );

    @PutMapping(value = "/{event-id}/images/{image-id}", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Обновление изображения мероприятия",
            description = "Заменяет изображение по ID мероприятия и ID изображения. Размер — до 1 МБ. Доступ: ORGANIZER если создавал, ADMIN",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Изображение успешно обновлено"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Мероприятие или изображение не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void updateImage(
            @PathVariable("event-id") Long eventId,
            @PathVariable("image-id") Long imageId,
            @RequestParam("image") MultipartFile image
    );

    @DeleteMapping("/{event-id}/images/{image-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление изображения мероприятия",
            description = "Удаляет изображение по ID мероприятия и ID изображения. Доступ: ORGANIZER если создавал, ADMIN",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Изображение удалено"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Мероприятие или изображение не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void deleteImage(
            @PathVariable("event-id") Long eventId,
            @PathVariable("image-id") Long imageId
    );

    @PutMapping(value = "/{event-id}/video", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Обновление видео мероприятия",
            description = "Обновляет видео по ID мероприятия. Размер — до 10 МБ. Доступ: ORGANIZER если создавал, ADMIN",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Видео обновлено"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Мероприятие не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void updateVideo(
            @PathVariable("event-id") Long eventId,
            @RequestParam("video") MultipartFile video
    );

    @DeleteMapping("/{event-id}/video")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление видео мероприятия",
            description = "Удаляет видео, связанное с мероприятием по ID. Доступ: ORGANIZER если создавал, ADMIN",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Видео удалено"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Недостаточно прав",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Мероприятие или видео не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void deleteVideo(
            @PathVariable("event-id") Long eventId
    );
}
