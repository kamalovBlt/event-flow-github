package com.technokratos.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;


@ConfigurationProperties(prefix = "logging.filter")
@Getter@Setter
public class LoggingFilterProperties {
    private int order = Ordered.HIGHEST_PRECEDENCE + 100;

}
