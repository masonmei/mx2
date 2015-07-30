// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

public class ErrorStatus extends StatusBase
{
    public ErrorStatus(final String msg, final Object origin) {
        super(2, msg, origin);
    }
    
    public ErrorStatus(final String msg, final Object origin, final Throwable t) {
        super(2, msg, origin, t);
    }
}
