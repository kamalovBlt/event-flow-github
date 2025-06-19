package com.technokratos.dto;

public record VerifyCodeDTO(
        String email,
        String code
) {
}
