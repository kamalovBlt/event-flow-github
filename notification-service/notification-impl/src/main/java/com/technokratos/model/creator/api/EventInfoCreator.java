package com.technokratos.model.creator.api;

import com.technokratos.dto.EventInfoDTO;
import com.technokratos.model.EventInfo;

public interface EventInfoCreator {
    EventInfo create(EventInfoDTO eventInfoDTO);
}
