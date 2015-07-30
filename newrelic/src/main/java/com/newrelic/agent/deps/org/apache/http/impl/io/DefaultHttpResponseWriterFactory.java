// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import com.newrelic.agent.deps.org.apache.http.io.HttpMessageWriter;
import com.newrelic.agent.deps.org.apache.http.io.SessionOutputBuffer;
import com.newrelic.agent.deps.org.apache.http.message.BasicLineFormatter;
import com.newrelic.agent.deps.org.apache.http.message.LineFormatter;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.io.HttpMessageWriterFactory;

@Immutable
public class DefaultHttpResponseWriterFactory implements HttpMessageWriterFactory<HttpResponse>
{
    public static final DefaultHttpResponseWriterFactory INSTANCE;
    private final LineFormatter lineFormatter;
    
    public DefaultHttpResponseWriterFactory(final LineFormatter lineFormatter) {
        this.lineFormatter = ((lineFormatter != null) ? lineFormatter : BasicLineFormatter.INSTANCE);
    }
    
    public DefaultHttpResponseWriterFactory() {
        this(null);
    }
    
    public HttpMessageWriter<HttpResponse> create(final SessionOutputBuffer buffer) {
        return new DefaultHttpResponseWriter(buffer, this.lineFormatter);
    }
    
    static {
        INSTANCE = new DefaultHttpResponseWriterFactory();
    }
}
