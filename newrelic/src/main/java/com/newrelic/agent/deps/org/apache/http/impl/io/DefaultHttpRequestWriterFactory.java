// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import com.newrelic.agent.deps.org.apache.http.io.HttpMessageWriter;
import com.newrelic.agent.deps.org.apache.http.io.SessionOutputBuffer;
import com.newrelic.agent.deps.org.apache.http.message.BasicLineFormatter;
import com.newrelic.agent.deps.org.apache.http.message.LineFormatter;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.io.HttpMessageWriterFactory;

@Immutable
public class DefaultHttpRequestWriterFactory implements HttpMessageWriterFactory<HttpRequest>
{
    public static final DefaultHttpRequestWriterFactory INSTANCE;
    private final LineFormatter lineFormatter;
    
    public DefaultHttpRequestWriterFactory(final LineFormatter lineFormatter) {
        this.lineFormatter = ((lineFormatter != null) ? lineFormatter : BasicLineFormatter.INSTANCE);
    }
    
    public DefaultHttpRequestWriterFactory() {
        this(null);
    }
    
    public HttpMessageWriter<HttpRequest> create(final SessionOutputBuffer buffer) {
        return new DefaultHttpRequestWriter(buffer, this.lineFormatter);
    }
    
    static {
        INSTANCE = new DefaultHttpRequestWriterFactory();
    }
}
