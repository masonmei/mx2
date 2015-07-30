// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

public interface ISqlStatementTracer
{
    Object getSql();
    
    void setExplainPlan(Object... p0);
}
