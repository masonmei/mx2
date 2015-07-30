// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;

public class CachingDateFormatter
{
    long lastTimestamp;
    String cachedStr;
    final SimpleDateFormat sdf;
    
    public CachingDateFormatter(final String pattern) {
        this.lastTimestamp = -1L;
        this.cachedStr = null;
        this.sdf = new SimpleDateFormat(pattern);
    }
    
    public final String format(final long now) {
        synchronized (this) {
            if (now != this.lastTimestamp) {
                this.lastTimestamp = now;
                this.cachedStr = this.sdf.format(new Date(now));
            }
            return this.cachedStr;
        }
    }
    
    public void setTimeZone(final TimeZone tz) {
        this.sdf.setTimeZone(tz);
    }
}
