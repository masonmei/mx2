// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import java.util.Iterator;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;

public class MDCConverter extends ClassicConverter
{
    String key;
    private static final String EMPTY_STRING = "";
    
    public void start() {
        this.key = this.getFirstOption();
        super.start();
    }
    
    public void stop() {
        this.key = null;
        super.stop();
    }
    
    public String convert(final ILoggingEvent event) {
        final Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
        if (mdcPropertyMap == null) {
            return "";
        }
        if (this.key == null) {
            return this.outputMDCForAllKeys(mdcPropertyMap);
        }
        final String value = event.getMDCPropertyMap().get(this.key);
        if (value != null) {
            return value;
        }
        return "";
    }
    
    private String outputMDCForAllKeys(final Map<String, String> mdcPropertyMap) {
        final StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (final Map.Entry<String, String> entry : mdcPropertyMap.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                buf.append(", ");
            }
            buf.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return buf.toString();
    }
}
