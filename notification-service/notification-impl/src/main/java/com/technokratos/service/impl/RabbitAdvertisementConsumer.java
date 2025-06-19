package com.technokratos.service.impl;

import com.technokratos.dto.EventInfoDTO;
import com.technokratos.model.EventInfo;
import com.technokratos.model.creator.api.EventInfoCreator;
import com.technokratos.service.api.AdvertisementConsumer;
import com.technokratos.service.api.AdvertisementService;
import com.technokratos.util.RabbitVariables;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitAdvertisementConsumer implements AdvertisementConsumer {

    private final AdvertisementService advertisementService;
    private final EventInfoCreator eventInfoCreator;

    @Override
    @RabbitListener(queues = {RabbitVariables.EMAIL_ADVERTISEMENT_QUEUE_NAME})
    public void receiveAdvertisementInfo(@Valid EventInfoDTO eventInfoDto) {
        EventInfo eventInfo = eventInfoCreator.create(eventInfoDto);
        advertisementService.sendAdvertisement(eventInfo, eventInfoDto.email());
    }

}
