package com.technokratos.model.creator.impl;

import com.technokratos.dto.EventInfoDTO;
import com.technokratos.model.EventInfo;
import com.technokratos.model.creator.api.EventInfoCreator;
import org.springframework.stereotype.Component;

@Component
public class BaseEventInfoCreator implements EventInfoCreator {

    @Override
    public EventInfo create(EventInfoDTO eventInfoDTO) {
        return EventInfo.builder()
                .name(eventInfoDTO.eventName())
                .time(eventInfoDTO.time())
                .artistsNames(eventInfoDTO.artistsName())
                .url(eventInfoDTO.url())
                .build();
    }

}
