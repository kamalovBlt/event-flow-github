package com.technokratos.client;

import com.technokratos.exception.EmailOrLoginNotValidException;
import com.technokratos.exception.BadCredentialsException;
import com.technokratos.exception.ExternalServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class UserClientErrorsDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        return switch (response.status()) {
            case 400 -> new EmailOrLoginNotValidException("Неправильный формат электронной почты");
            case 404 -> new BadCredentialsException("""
                    Пользователь с такими данными не найден""");
            case 500 -> new ExternalServiceException(
                    "Ошибка внутреннего сервиса"
            );
            default -> new Exception();
        };
    }

}
