package com.technokratos.config;

import com.technokratos.util.ConsulIpChecker;
import com.technokratos.util.IpRestrictedRequestMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfiguration {

    private final ConsulIpChecker ipChecker;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new IpRestrictedRequestMatcher(ipChecker, List.of(
                                "/.well-known/jwks.json",
                                "/api/v1/auth-service/auth/access-token"
                        ))).denyAll()

                        .anyRequest().permitAll()
                )
        ;
        return http.build();
    }

}
