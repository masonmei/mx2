// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.api.agent.OutboundHeaders;

public class NoOpCrossProcessState implements CrossProcessState
{
    public static final CrossProcessState INSTANCE;
    
    public void processOutboundRequestHeaders(final OutboundHeaders outboundHeaders) {
    }
    
    public void processOutboundResponseHeaders(final OutboundHeaders outboundHeaders, final long contentLength) {
    }
    
    public String getRequestMetadata() {
        return null;
    }
    
    public void processRequestMetadata(final String requestMetadata) {
    }
    
    public String getResponseMetadata() {
        return null;
    }
    
    public void processResponseMetadata(final String responseMetadata) {
    }
    
    public void processInboundResponseHeaders(final InboundHeaders inboundHeaders, final TracedMethod tracer, final String host, final String uri, final boolean addRollupMetric) {
    }
    
    static {
        INSTANCE = new NoOpCrossProcessState();
    }
}
