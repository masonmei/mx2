// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.api.agent.OutboundHeaders;

public interface CrossProcessState
{
    void processOutboundRequestHeaders(OutboundHeaders p0);
    
    void processOutboundResponseHeaders(OutboundHeaders p0, long p1);
    
    void processInboundResponseHeaders(InboundHeaders p0, TracedMethod p1, String p2, String p3, boolean p4);
    
    String getRequestMetadata();
    
    void processRequestMetadata(String p0);
    
    String getResponseMetadata();
    
    void processResponseMetadata(String p0);
}
