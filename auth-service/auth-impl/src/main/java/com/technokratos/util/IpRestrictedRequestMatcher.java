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

        String remoteIp = request.getRemoteAddr();
        return !ipChecker.isAllowed(remoteIp);
    }
}
