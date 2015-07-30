// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import com.newrelic.agent.instrumentation.tracing.TraceDetails;

public interface TraceClassAndMethodMatcher extends ClassAndMethodMatcher
{
    TraceDetails getTraceDetails();
}
