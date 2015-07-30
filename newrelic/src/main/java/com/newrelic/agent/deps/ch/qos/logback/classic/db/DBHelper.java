// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.db;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;

public class DBHelper
{
    public static final short PROPERTIES_EXIST = 1;
    public static final short EXCEPTION_EXISTS = 2;
    
    public static short computeReferenceMask(final ILoggingEvent event) {
        short mask = 0;
        int mdcPropSize = 0;
        if (event.getMDCPropertyMap() != null) {
            mdcPropSize = event.getMDCPropertyMap().keySet().size();
        }
        int contextPropSize = 0;
        if (event.getLoggerContextVO().getPropertyMap() != null) {
            contextPropSize = event.getLoggerContextVO().getPropertyMap().size();
        }
        if (mdcPropSize > 0 || contextPropSize > 0) {
            mask = 1;
        }
        if (event.getThrowableProxy() != null) {
            mask |= 0x2;
        }
        return mask;
    }
}
