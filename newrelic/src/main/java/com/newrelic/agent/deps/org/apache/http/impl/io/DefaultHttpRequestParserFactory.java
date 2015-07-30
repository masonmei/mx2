// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import com.newrelic.agent.deps.org.apache.http.io.HttpMessageParser;
import com.newrelic.agent.deps.org.apache.http.config.MessageConstraints;
import com.newrelic.agent.deps.org.apache.http.io.SessionInputBuffer;
import com.newrelic.agent.deps.org.apache.http.impl.DefaultHttpRequestFactory;
import com.newrelic.agent.deps.org.apache.http.message.BasicLineParser;
import com.newrelic.agent.deps.org.apache.http.HttpRequestFactory;
import com.newrelic.agent.deps.org.apache.http.message.LineParser;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.io.HttpMessageParserFactory;

@Immutable
public class DefaultHttpRequestParserFactory implements HttpMessageParserFactory<HttpRequest>
{
    public static final DefaultHttpRequestParserFactory INSTANCE;
    private final LineParser lineParser;
    private final HttpRequestFactory requestFactory;
    
    public DefaultHttpRequestParserFactory(final LineParser lineParser, final HttpRequestFactory requestFactory) {
        this.lineParser = ((lineParser != null) ? lineParser : BasicLineParser.INSTANCE);
        this.requestFactory = ((requestFactory != null) ? requestFactory : DefaultHttpRequestFactory.INSTANCE);
    }
    
    public DefaultHttpRequestParserFactory() {
        this(null, null);
    }
    
    public HttpMessageParser<HttpRequest> create(final SessionInputBuffer buffer, final MessageConstraints constraints) {
        return new DefaultHttpRequestParser(buffer, this.lineParser, this.requestFactory, constraints);
    }
    
    static {
        INSTANCE = new DefaultHttpRequestParserFactory();
    }
}
