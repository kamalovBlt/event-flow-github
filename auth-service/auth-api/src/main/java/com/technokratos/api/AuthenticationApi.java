package com.technokratos.api;

import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.GoogleAuthorizationCodeRequest;
import com.technokratos.dto.request.GoogleRegisterRequest;
import com.technokratos.dto.request.RefreshTokenRequest;
import com.technokratos.dto.response.AuthServiceErrorResponse;
import com.technokratos.dto.response.AuthenticationResponse;
import com.technokratos.dto.response.GoogleUserNotFoundInformationResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Аутентификация", description = "Операции входа, обновления токена и OAuth")
public interface AuthenticationApi {

    @Operation(summary = "Вход с использованием логина и пароля")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешная аутентификация",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные",
                    content = @Content(schema = @Schema(implementation = AuthServiceErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Неправильный логин/неправильный формат учетных данных",
                    content = @Content(schema = @Schema(implementation = AuthServiceErrorResponse.class))),
    })
    @PostMapping("/api/v1/auth-service/auth/login")
    @ResponseStatus(HttpStatus.CREATED)
    AuthenticationResponse login(
            @RequestBody AuthenticationRequest authenticationRequest
    );

    @Operation(summary = "Обновить access токен с использованием refresh токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Новый access токен выдан",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Недействительный refresh токен")
    })
    @PostMapping("/api/v1/auth-service/auth/refresh")
    @ResponseStatus(HttpStatus.OK)
    AuthenticationResponse refresh(@RequestBody RefreshTokenRequest request);

    @GetMapping("/.well-known/jwks.json")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    Map<String, Object> key();

    @GetMapping("/api/v1/auth-service/auth/access-token")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    String toServiceAccessToken();

    @Operation(summary = "OAuth вход через Google",
            description = """
                    Получение access токена через авторизационный код от Google,
                    если пользователь не сохранен, то возвращает GoogleUserNotFoundInformationRequest,
                    затем нужно кинуть запрос на /api/v1/auth-service/auth/login/google/new взяв информацию
                    оттуда
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешная аутентификация через Google",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации, неверный или просроченный код авторизации",
                    content = @Content(schema = @Schema(implementation = AuthServiceErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден, воспользуйтесь /api/v1/auth-service/auth/login/google/new",
                    content = @Content(schema = @Schema(implementation = GoogleUserNotFoundInformationResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь уже существует",
                    content = @Content(schema = @Schema(implementation = GoogleUserNotFoundInformationResponse.class))),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/auth-service/auth/login/google")
    AuthenticationResponse loginViaGoogle(
            @RequestBody GoogleAuthorizationCodeRequest googleAuthorizationCodeRequest
    );

    @Operation(summary = "OAuth вход через Google",
            description = """
                    Сохраняет пользователя как через google и возвращает JWT токены
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешная аутентификация через Google",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации, неверный или просроченный код авторизации",
                    content = @Content(schema = @Schema(implementation = AuthServiceErrorResponse.class))),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/auth-service/auth/login/google/new")
    AuthenticationResponse loginViaGoogleNew(
            @RequestBody GoogleRegisterRequest googleRegisterRequest
    );

}
