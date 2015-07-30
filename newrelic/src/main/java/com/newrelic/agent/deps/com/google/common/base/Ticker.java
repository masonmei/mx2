// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.base;

import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class Ticker
{
    private static final Ticker SYSTEM_TICKER;
    
    public abstract long read();
    
    public static Ticker systemTicker() {
        return Ticker.SYSTEM_TICKER;
    }
    
    static {
        SYSTEM_TICKER = new Ticker() {
            @Override
            public long read() {
                return Platform.systemNanoTime();
            }
        };
    }
}
