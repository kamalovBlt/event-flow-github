package com.technokratos.client;

import com.technokratos.api.UserApi;
import com.technokratos.config.feign.FeignUserClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service", configuration = FeignUserClientConfiguration.class)
public interface UserClient extends UserApi {}


