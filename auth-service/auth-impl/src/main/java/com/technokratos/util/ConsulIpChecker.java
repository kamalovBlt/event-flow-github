package com.technokratos.util;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import com.ecwid.consul.v1.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class ConsulIpChecker {

    private final ConsulClient consulClient;

    private Set<String> allowedIps = new HashSet<>();
    private long lastUpdated = 0;
    private static final long TTL = 30_000;

    public boolean isAllowed(String ip) {
        refreshCacheIfNeeded();
        return allowedIps.contains(ip);
    }

    private synchronized void refreshCacheIfNeeded() {
        if (System.currentTimeMillis() - lastUpdated < TTL) return;

        Set<String> newIps = new HashSet<>();
        List<String> serviceNames = List.of("user-service", "event-service", "location-service");

        for (String serviceName : serviceNames) {
            Response<List<HealthService>> response = consulClient.getHealthServices(
                    serviceName,
                    HealthServicesRequest.newBuilder().setPassing(true).build()
            );

            List<HealthService> services = response.getValue();

            for (HealthService service : services) {
                String address = service.getService().getAddress();
                if (address == null || address.isEmpty()) {
                    address = service.getNode().getAddress();
                }
                if (address != null && !address.isEmpty()) {
                    newIps.add(address);
                }
            }
        }

        this.allowedIps = newIps;
        this.lastUpdated = System.currentTimeMillis();
    }
}
