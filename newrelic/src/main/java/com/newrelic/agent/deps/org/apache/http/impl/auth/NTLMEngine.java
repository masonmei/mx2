// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.auth;

public interface NTLMEngine
{
    String generateType1Msg(String p0, String p1) throws NTLMEngineException;
    
    String generateType3Msg(String p0, String p1, String p2, String p3, String p4) throws NTLMEngineException;
}
