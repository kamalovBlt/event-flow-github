package com.technokratos.service.api;

import com.technokratos.dto.EventInfoDTO;

public interface AdvertisementConsumer {

    void receiveAdvertisementInfo(EventInfoDTO eventInfoDto);

}
