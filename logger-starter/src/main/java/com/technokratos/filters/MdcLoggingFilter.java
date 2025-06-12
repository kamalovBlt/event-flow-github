package com.technokratos.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;

public class MdcLoggingFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain)
            throws IOException, ServletException {

        try {
            MDC.put("ip", request.getRemoteAddr());
            MDC.put("uri", request.getRequestURI());
            MDC.put("method", request.getMethod());
            MDC.put("agent", request.getHeader("User-Agent"));

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
