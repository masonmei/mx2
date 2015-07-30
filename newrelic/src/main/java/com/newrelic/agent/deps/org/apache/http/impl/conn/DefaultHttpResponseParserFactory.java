// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import com.newrelic.agent.deps.org.apache.http.io.HttpMessageParser;
import com.newrelic.agent.deps.org.apache.http.config.MessageConstraints;
import com.newrelic.agent.deps.org.apache.http.io.SessionInputBuffer;
import com.newrelic.agent.deps.org.apache.http.impl.DefaultHttpResponseFactory;
import com.newrelic.agent.deps.org.apache.http.message.BasicLineParser;
import com.newrelic.agent.deps.org.apache.http.HttpResponseFactory;
import com.newrelic.agent.deps.org.apache.http.message.LineParser;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.io.HttpMessageParserFactory;

@Immutable
public class DefaultHttpResponseParserFactory implements HttpMessageParserFactory<HttpResponse>
{
    public static final DefaultHttpResponseParserFactory INSTANCE;
    private final LineParser lineParser;
    private final HttpResponseFactory responseFactory;
    
    public DefaultHttpResponseParserFactory(final LineParser lineParser, final HttpResponseFactory responseFactory) {
        this.lineParser = ((lineParser != null) ? lineParser : BasicLineParser.INSTANCE);
        this.responseFactory = ((responseFactory != null) ? responseFactory : DefaultHttpResponseFactory.INSTANCE);
    }
    
    public DefaultHttpResponseParserFactory(final HttpResponseFactory responseFactory) {
        this(null, responseFactory);
    }
    
    public DefaultHttpResponseParserFactory() {
        this(null, null);
    }
    
    public HttpMessageParser<HttpResponse> create(final SessionInputBuffer buffer, final MessageConstraints constraints) {
        return new DefaultHttpResponseParser(buffer, this.lineParser, this.responseFactory, constraints);
    }
    
    static {
        INSTANCE = new DefaultHttpResponseParserFactory();
    }
}
