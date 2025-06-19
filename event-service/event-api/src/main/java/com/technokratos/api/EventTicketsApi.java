package com.technokratos.api;

import com.technokratos.dto.request.ticket.TicketFullRequest;
import com.technokratos.dto.response.EventServiceErrorResponse;
import com.technokratos.dto.response.ticket.PaymentLinkResponse;
import com.technokratos.dto.response.ticket.TicketFullResponse;
import com.technokratos.dto.response.ticket.TicketsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/event-service/events")
@Tag(
        name = "Мероприятия",
        description = "Операции с билетами мероприятий"
)
public interface EventTicketsApi {

    @GetMapping("/{event-id}/tickets")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Поиск билетов",
            description = """
                    Возвращает список билетов с их ID
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список билетов успешно получен",
                            content = @Content(schema = @Schema(implementation = TicketsResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет доступа, авторизуйтесь",
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
    TicketsResponse findTickets(@Parameter @PathVariable("event-id") Long eventId);

    @GetMapping("/{event-id}/tickets/{ticket-id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Поиск билета по ID",
            description = "Возвращает информацию о билете по его ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Билет найден",
                            content = @Content(schema = @Schema(implementation = TicketFullResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет доступа, авторизуйтесь",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Билет или мероприятие не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка сервера",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    TicketFullResponse findTicketById(
            @Parameter @PathVariable("event-id") Long eventId,
            @Parameter @PathVariable("ticket-id") Long ticketId
    );

    @PostMapping("/{event-id}/tickets/{ticket-id}/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создание платежа за билет",
            description = """
            Создает платеж за билет и возвращает ссылку для оплаты.
            Максимум на одном аккаунте - 10 билетов.
            Доступ: USER, ORGANIZER, PLATFORM, ADMIN
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Платеж создан, ссылка на оплату возвращена",
                            content = @Content(schema = @Schema(implementation = PaymentLinkResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступа нет, авторизуйтесь",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Билет или мероприятие не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Достигнут максимум билетов",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка сервера",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    PaymentLinkResponse purchase(
            @Parameter @PathVariable("event-id") Long eventId,
            @Parameter @PathVariable("ticket-id") Long ticketId
    );

    @PostMapping("/{event-id}/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Добавление нового билета",
            description = "Создает новый билет для мероприятия. Доступ: ORGANIZER если создавал, ADMIN",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Билет успешно добавлен",
                            content = @Content(schema = @Schema(implementation = TicketFullRequest.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступа нет, авторизуйтесь",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Мероприятие не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка сервера",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    Long addTicket(
            @Parameter @PathVariable("event-id") Long eventId,
            @Valid @RequestBody TicketFullRequest ticketFullRequest
            );

    @PutMapping("/{event-id}/tickets/{ticket-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Обновление информации о билете",
            description = "Обновляет информацию о билете для мероприятия. Доступ: ORGANIZER если создавал, ADMIN",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Информация о билете успешно обновлена"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступа нет, авторизуйтесь",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Билет или мероприятие не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка сервера",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void updateTicket(
            @Parameter @PathVariable("event-id") Long eventId,
            @Parameter @PathVariable("ticket-id") Long ticketId,
            @Valid @RequestBody TicketFullRequest ticketFullRequest
    );

    @DeleteMapping("/{event-id}/tickets/{ticket-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление билета",
            description = "Удаляет билет с мероприятия. Доступ: ORGANIZER если создавал, ADMIN",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Билет успешно удален"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступа нет, авторизуйтесь",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Билет или мероприятие не найдено",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка сервера",
                            content = @Content(schema = @Schema(implementation = EventServiceErrorResponse.class))
                    )
            }
    )
    void deleteTicket(
            @Parameter @PathVariable("event-id") Long eventId,
            @Parameter @PathVariable("ticket-id") Long ticketId
    );
}
