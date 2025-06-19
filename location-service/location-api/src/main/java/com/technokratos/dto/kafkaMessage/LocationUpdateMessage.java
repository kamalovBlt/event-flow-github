package com.technokratos.dto.kafkaMessage;

import java.util.List;

public record LocationUpdateMessage(
    List<UpdateSeatMessage> updatedSeats
) {}