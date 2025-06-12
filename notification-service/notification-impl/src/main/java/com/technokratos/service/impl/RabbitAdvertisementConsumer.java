package com.technokratos.service.impl;

import com.technokratos.config.RabbitConfiguration;
import com.technokratos.dto.EventInfoDTO;
import com.technokratos.model.EventInfo;
import com.technokratos.model.creator.api.EventInfoCreator;
import com.technokratos.service.api.AdvertisementConsumer;
import com.technokratos.service.api.AdvertisementService;
import com.technokratos.util.RabbitVariables;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitAdvertisementConsumer implements AdvertisementConsumer {

    private final AdvertisementService advertisementService;
    private final EventInfoCreator eventInfoCreator;

    @Override
    @RabbitListener(queues = {RabbitVariables.EMAIL_ADVERTISEMENT_QUEUE_NAME})
    public void receiveAdvertisementInfo(@Valid EventInfoDTO eventInfoDto) {
        try {
            EventInfo eventInfo = eventInfoCreator.create(eventInfoDto);
            advertisementService.sendAdvertisement(eventInfo, eventInfoDto.email());
        } catch (MethodArgumentNotValidException e) {
            log.info("Не удалось отправить рассылку {}", eventInfoDto);
        } catch (Exception e) {
            log.warn("Ошибка при обработке сообщения {}", eventInfoDto);
        }

    }

}
