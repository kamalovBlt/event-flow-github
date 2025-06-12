package com.technokratos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record EventInfoDTO(
        @Email
        String email,
        @NotBlank
        String eventName,
        @NotNull
        LocalDateTime time,
        List<String> artistsName,
        @NotBlank
        String url
) {
}
