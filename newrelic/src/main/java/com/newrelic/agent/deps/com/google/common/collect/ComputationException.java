// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public class ComputationException extends RuntimeException
{
    private static final long serialVersionUID = 0L;
    
    public ComputationException(@Nullable final Throwable cause) {
        super(cause);
    }
}
