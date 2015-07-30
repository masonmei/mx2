// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.context;

import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;

public interface TraceDetailsList
{
    void addTrace(Method p0, TraceDetails p1);
}
