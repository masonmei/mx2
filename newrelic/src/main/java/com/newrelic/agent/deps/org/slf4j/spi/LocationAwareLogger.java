// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j.spi;

import com.newrelic.agent.deps.org.slf4j.Marker;
import com.newrelic.agent.deps.org.slf4j.Logger;

public interface LocationAwareLogger extends Logger
{
    public static final int TRACE_INT = 0;
    public static final int DEBUG_INT = 10;
    public static final int INFO_INT = 20;
    public static final int WARN_INT = 30;
    public static final int ERROR_INT = 40;
    
    void log(Marker p0, String p1, int p2, String p3, Object[] p4, Throwable p5);
}
