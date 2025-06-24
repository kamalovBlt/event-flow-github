package com.technokratos.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@RequiredArgsConstructor
public class IpRestrictedRequestMatcher implements RequestMatcher {

    private final ConsulIpChecker ipChecker;
    private final List<String> protectedPaths;

    @Override
    public boolean matches(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (protectedPaths.stream().noneMatch(uri::startsWith)) return false;

        String remoteIp = extractClientIp(request);
        return ipChecker.isAllowed(remoteIp);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}
