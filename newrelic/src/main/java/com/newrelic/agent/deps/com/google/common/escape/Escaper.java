// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.escape;

import com.newrelic.agent.deps.com.google.common.base.Function;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public abstract class Escaper
{
    private final Function<String, String> asFunction;
    
    protected Escaper() {
        this.asFunction = new Function<String, String>() {
            @Override
            public String apply(final String from) {
                return Escaper.this.escape(from);
            }
        };
    }
    
    public abstract String escape(final String p0);
    
    public final Function<String, String> asFunction() {
        return this.asFunction;
    }
}
