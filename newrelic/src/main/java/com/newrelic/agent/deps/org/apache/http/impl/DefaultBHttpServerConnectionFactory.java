// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl;

import com.newrelic.agent.deps.org.apache.http.HttpConnection;
import java.io.IOException;
import java.net.Socket;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.io.HttpMessageWriterFactory;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.io.HttpMessageParserFactory;
import com.newrelic.agent.deps.org.apache.http.entity.ContentLengthStrategy;
import com.newrelic.agent.deps.org.apache.http.config.ConnectionConfig;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpConnectionFactory;

@Immutable
public class DefaultBHttpServerConnectionFactory implements HttpConnectionFactory<DefaultBHttpServerConnection>
{
    public static final DefaultBHttpServerConnectionFactory INSTANCE;
    private final ConnectionConfig cconfig;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;
    private final HttpMessageParserFactory<HttpRequest> requestParserFactory;
    private final HttpMessageWriterFactory<HttpResponse> responseWriterFactory;
    
    public DefaultBHttpServerConnectionFactory(final ConnectionConfig cconfig, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy, final HttpMessageParserFactory<HttpRequest> requestParserFactory, final HttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
        this.cconfig = ((cconfig != null) ? cconfig : ConnectionConfig.DEFAULT);
        this.incomingContentStrategy = incomingContentStrategy;
        this.outgoingContentStrategy = outgoingContentStrategy;
        this.requestParserFactory = requestParserFactory;
        this.responseWriterFactory = responseWriterFactory;
    }
    
    public DefaultBHttpServerConnectionFactory(final ConnectionConfig cconfig, final HttpMessageParserFactory<HttpRequest> requestParserFactory, final HttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
        this(cconfig, null, null, requestParserFactory, responseWriterFactory);
    }
    
    public DefaultBHttpServerConnectionFactory(final ConnectionConfig cconfig) {
        this(cconfig, null, null, null, null);
    }
    
    public DefaultBHttpServerConnectionFactory() {
        this(null, null, null, null, null);
    }
    
    public DefaultBHttpServerConnection createConnection(final Socket socket) throws IOException {
        final DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(this.cconfig.getBufferSize(), this.cconfig.getFragmentSizeHint(), ConnSupport.createDecoder(this.cconfig), ConnSupport.createEncoder(this.cconfig), this.cconfig.getMessageConstraints(), this.incomingContentStrategy, this.outgoingContentStrategy, this.requestParserFactory, this.responseWriterFactory);
        conn.bind(socket);
        return conn;
    }
    
    static {
        INSTANCE = new DefaultBHttpServerConnectionFactory();
    }
}
