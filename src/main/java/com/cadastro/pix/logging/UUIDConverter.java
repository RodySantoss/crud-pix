package com.cadastro.pix.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

public class UUIDConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return MDC.get("reqId");
    }

    public static void register() {
        PatternLayout.defaultConverterMap.put("reqId", UUIDConverter.class.getName());
    }
}
