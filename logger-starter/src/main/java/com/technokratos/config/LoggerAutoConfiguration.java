package com.technokratos.config;

import com.technokratos.aspects.ControllerServiceRepositoryLoggingAspect;
import com.technokratos.aspects.LoggingAnnotationAspect;
import com.technokratos.config.property.LoggingFilterProperties;
import com.technokratos.filters.MdcLoggingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LoggingFilterProperties.class)
@ConditionalOnProperty(prefix = "logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ControllerServiceRepositoryLoggingAspect.class)
    public ControllerServiceRepositoryLoggingAspect controllerLoggingAspect() {
        return new ControllerServiceRepositoryLoggingAspect();
    }

    @Bean
    @ConditionalOnMissingBean(LoggingAnnotationAspect.class)
    public LoggingAnnotationAspect loggingAnnotationAspect() {
        return new LoggingAnnotationAspect();
    }

    @Bean
    @ConditionalOnMissingBean(MdcLoggingFilter.class)
    public FilterRegistrationBean<MdcLoggingFilter> requestLoggingFilter(LoggingFilterProperties loggingFilterProperties) {
        FilterRegistrationBean<MdcLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(mdcLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(loggingFilterProperties.getOrder());
        return registrationBean;
    }
    @Bean
    public MdcLoggingFilter mdcLoggingFilter() {
        return new MdcLoggingFilter();
    }

}
