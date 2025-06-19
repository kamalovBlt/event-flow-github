package com.technokratos.kafka;

import com.technokratos.dto.kafkaMessage.UpdateSeatMessage;
import com.technokratos.dto.kafkaMessage.LocationUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationEventProducer {

    private final KafkaTemplate<String, LocationUpdateMessage> kafkaTemplate;

    @Transactional("kafkaTransactionManager")
    public void sendUpdateSeat(List<UpdateSeatMessage> updateSeats) {
        kafkaTemplate.executeInTransaction(kt -> {
            kt.send("location-update", new LocationUpdateMessage(updateSeats));
            return true;
        });
    }
}
