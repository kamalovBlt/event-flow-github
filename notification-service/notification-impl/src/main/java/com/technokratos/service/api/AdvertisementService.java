package com.technokratos.service.api;

import com.technokratos.model.EventInfo;

public interface AdvertisementService {

    void sendAdvertisement(EventInfo eventInfo, String contact);

}
