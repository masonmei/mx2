// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;
import java.util.HashMap;

public class OutboundHeadersMap extends HashMap<String, String> implements OutboundHeaders
{
    private final HeaderType type;
    
    public OutboundHeadersMap(final HeaderType type) {
        this.type = type;
    }
    
    public HeaderType getHeaderType() {
        return this.type;
    }
    
    public void setHeader(final String name, final String value) {
        this.put(name, value);
    }
}
