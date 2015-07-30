// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.gson;

public interface ExclusionStrategy
{
    boolean shouldSkipField(FieldAttributes p0);
    
    boolean shouldSkipClass(Class<?> p0);
}
