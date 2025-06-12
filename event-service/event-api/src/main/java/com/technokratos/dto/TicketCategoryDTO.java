package com.technokratos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Категория билета")
public enum TicketCategoryDTO {
    COMMON, VIP
}
