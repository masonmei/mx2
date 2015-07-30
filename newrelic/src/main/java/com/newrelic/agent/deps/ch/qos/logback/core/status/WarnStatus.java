// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

public class WarnStatus extends StatusBase
{
    public WarnStatus(final String msg, final Object origin) {
        super(1, msg, origin);
    }
    
    public WarnStatus(final String msg, final Object origin, final Throwable t) {
        super(1, msg, origin, t);
    }
}
