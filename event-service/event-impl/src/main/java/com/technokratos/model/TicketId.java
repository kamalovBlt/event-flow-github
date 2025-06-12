package com.technokratos.model;

public record TicketId(Long userId, Long eventId, String locationId, Long hallId, Long rowId, Long seatId) {}
