// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.io.UnsupportedEncodingException;
import com.newrelic.agent.util.Obfuscator;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class DeobfuscatedInboundHeaders implements InboundHeaders
{
    InboundHeaders delegate;
    String encodingKey;
    
    public DeobfuscatedInboundHeaders(final InboundHeaders headers, final String encodingKey) {
        this.delegate = headers;
        this.encodingKey = encodingKey;
    }
    
    public HeaderType getHeaderType() {
        return this.delegate.getHeaderType();
    }
    
    public String getHeader(final String name) {
        if (this.encodingKey == null) {
            return null;
        }
        if (HeadersUtil.NEWRELIC_HEADERS.contains(name)) {
            final String obfuscatedValue = this.delegate.getHeader(name);
            if (obfuscatedValue == null) {
                return null;
            }
            try {
                return Obfuscator.deobfuscateNameUsingKey(obfuscatedValue, this.encodingKey);
            }
            catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        return this.delegate.getHeader(name);
    }
}
