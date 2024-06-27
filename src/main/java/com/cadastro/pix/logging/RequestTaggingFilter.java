package com.cadastro.pix.logging;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class RequestTaggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTaggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MDC.put("reqId", UUID.randomUUID().toString());

        LOGGER.info("Start of request");

        filterChain.doFilter(servletRequest, servletResponse);

        LOGGER.info("End of request");
    }
}