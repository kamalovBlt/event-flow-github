package com.technokratos.dto.response.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ссылка на оплату")
public record PaymentLinkResponse(
        @Schema(description = "URL, по которому пользователь должен перейти для оплаты")
        String confirmationUrl
) {}