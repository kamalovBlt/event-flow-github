package com.technokratos.api;

import com.technokratos.dto.request.UserRequest;
import com.technokratos.dto.request.UserWithOAuthRequest;
import com.technokratos.dto.response.UserDetailsResponse;
import com.technokratos.dto.response.UserServiceErrorResponse;
import com.technokratos.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Пользователи",
        description = "Операции с пользователями"
)
public interface UserApi {

    @GetMapping("/api/v1/user-service/users/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получения пользователя по ID",
            description = """
                    Параметр - id пользователя типа long,
                    указывается в URI, возвращает все данные о пользователе.
                    Доступ: ADMIN || пользователь который имеет такой ID
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь найден",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неправильный формат ID",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, возвращает название исключения",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    )
            }
    )
    UserResponse findById(
            @Parameter(
                    description = "ID пользователя (число), указывается в URI",
                    required = true
            )
            @PathVariable("user-id")
            Long id
    );

    @GetMapping("/api/v1/user-service/users/email")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    UserDetailsResponse findByEmail(
            @RequestParam("email") String email
    );

    @GetMapping("/api/v1/user-service/users/{user-id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Поиск рекомендованных пользователей с пагинацией",
            description = """
                    Возвращает список рекомендованных пользователей для указанного пользователя.
                    Может вернуть и пустой список
                    Поддерживает параметры пагинации: page (номер страницы), size (размер страницы).
                    Доступ: USER.
                    Примечание: не реализован
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Рекомендации успешно получены",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserResponse.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неправильный формат ID",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    )
            }
    )
    List<UserResponse> getRecommendations(
            @Parameter(
                    description = "ID пользователя, для которого формируются рекомендации",
                    required = true
            )
            @PathVariable("user-id") Long userId,

            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы (количество пользователей на странице)")
            @RequestParam(defaultValue = "10") int size
    );

    @PostMapping("/api/v1/user-service/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Сохранение пользователя",
            description = "Тело запроса - объект UserDTO, возвращает id (long) сохраненного пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Пользователь успешно сохранен, тело ответа содержит id",
                            content = @Content(schema = @Schema(implementation = Long.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается названии исключения",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    )
            }
    )
    long save(@RequestBody UserRequest userRequest);

    @PostMapping("/api/v1/user-service/users/oauth")
    @ResponseStatus(HttpStatus.CREATED)
    @Hidden
    long saveOauthUser(@RequestBody UserWithOAuthRequest userWithOAuthRequest);

    @PutMapping("/api/v1/user-service/users/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Обновления пользователя",
            description = "Параметр - ID, тело запроса - полный объект UserDTO, обновляет пользователя. Доступ: пользователь который имеет такой ID",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Пользователь успешно обновлен"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь с таким ID не найден",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутрення ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    )
            }
    )
    void update(
            @Parameter(
                    description = "ID пользователя, которого требуется обновить, указывается в URI",
                    required = true
            )
            @PathVariable("user-id")
            Long id,
            @RequestBody UserRequest userRequest
    );

    @DeleteMapping("/api/v1/user-service/users/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удаление пользователя",
            description = "Доступ: пользователь который имеет такой ID",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Пользователь успешно обновлен"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос, ошибка описывается в теле ответа",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь с таким ID не найден",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера, указывается название исключения",
                            content = @Content(schema = @Schema(implementation = UserServiceErrorResponse.class))
                    )
            }
    )
    void delete(
            @Parameter(
                    description = "ID пользователя, которого требуется удалить, указывается в URI",
                    required = true
            )
            @PathVariable("user-id")
            Long id
    );

}
