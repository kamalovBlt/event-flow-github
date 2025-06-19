package com.technokratos.dto.kafkaMessage;

public record UpdateSeatMessage(
    String locationId,
    String hallName,
    Integer rowNum,
    Integer seatNum,
    SeatUpdateStatus status
) {}