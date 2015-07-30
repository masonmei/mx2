// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.entity;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.ProtocolVersion;
import com.newrelic.agent.deps.org.apache.http.HttpVersion;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.HttpMessage;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.entity.ContentLengthStrategy;

@Immutable
public class StrictContentLengthStrategy implements ContentLengthStrategy
{
    public static final StrictContentLengthStrategy INSTANCE;
    private final int implicitLen;
    
    public StrictContentLengthStrategy(final int implicitLen) {
        this.implicitLen = implicitLen;
    }
    
    public StrictContentLengthStrategy() {
        this(-1);
    }
    
    public long determineLength(final HttpMessage message) throws HttpException {
        Args.notNull(message, "HTTP message");
        final Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
        if (transferEncodingHeader == null) {
            final Header contentLengthHeader = message.getFirstHeader("Content-Length");
            if (contentLengthHeader != null) {
                final String s = contentLengthHeader.getValue();
                try {
                    final long len = Long.parseLong(s);
                    if (len < 0L) {
                        throw new ProtocolException("Negative content length: " + s);
                    }
                    return len;
                }
                catch (NumberFormatException e) {
                    throw new ProtocolException("Invalid content length: " + s);
                }
            }
            return this.implicitLen;
        }
        final String s2 = transferEncodingHeader.getValue();
        if ("chunked".equalsIgnoreCase(s2)) {
            if (message.getProtocolVersion().lessEquals(HttpVersion.HTTP_1_0)) {
                throw new ProtocolException("Chunked transfer encoding not allowed for " + message.getProtocolVersion());
            }
            return -2L;
        }
        else {
            if ("identity".equalsIgnoreCase(s2)) {
                return -1L;
            }
            throw new ProtocolException("Unsupported transfer encoding: " + s2);
        }
    }
    
    static {
        INSTANCE = new StrictContentLengthStrategy();
    }
}
