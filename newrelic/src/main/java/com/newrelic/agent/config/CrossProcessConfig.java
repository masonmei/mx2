// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

public interface CrossProcessConfig
{
    String getCrossProcessId();
    
    String getEncodedCrossProcessId();
    
    String getEncodingKey();
    
    boolean isTrustedAccountId(String p0);
    
    boolean isCrossApplicationTracing();
}
