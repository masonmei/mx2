// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.LoggerContextVO;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;

public final class PropertyConverter extends ClassicConverter
{
    String key;
    
    public void start() {
        final String optStr = this.getFirstOption();
        if (optStr != null) {
            this.key = optStr;
            super.start();
        }
    }
    
    public String convert(final ILoggingEvent event) {
        if (this.key == null) {
            return "Property_HAS_NO_KEY";
        }
        final LoggerContextVO lcvo = event.getLoggerContextVO();
        final Map<String, String> map = lcvo.getPropertyMap();
        final String val = map.get(this.key);
        if (val != null) {
            return val;
        }
        return System.getProperty(this.key);
    }
}
