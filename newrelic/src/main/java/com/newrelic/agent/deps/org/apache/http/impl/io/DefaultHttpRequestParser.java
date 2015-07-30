// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import com.newrelic.agent.deps.org.apache.http.HttpMessage;
import com.newrelic.agent.deps.org.apache.http.ParseException;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.RequestLine;
import com.newrelic.agent.deps.org.apache.http.message.ParserCursor;
import com.newrelic.agent.deps.org.apache.http.ConnectionClosedException;
import com.newrelic.agent.deps.org.apache.http.impl.DefaultHttpRequestFactory;
import com.newrelic.agent.deps.org.apache.http.config.MessageConstraints;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.message.LineParser;
import com.newrelic.agent.deps.org.apache.http.io.SessionInputBuffer;
import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;
import com.newrelic.agent.deps.org.apache.http.HttpRequestFactory;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;

@NotThreadSafe
public class DefaultHttpRequestParser extends AbstractMessageParser<HttpRequest>
{
    private final HttpRequestFactory requestFactory;
    private final CharArrayBuffer lineBuf;
    
    public DefaultHttpRequestParser(final SessionInputBuffer buffer, final LineParser lineParser, final HttpRequestFactory requestFactory, final HttpParams params) {
        super(buffer, lineParser, params);
        this.requestFactory = Args.notNull(requestFactory, "Request factory");
        this.lineBuf = new CharArrayBuffer(128);
    }
    
    public DefaultHttpRequestParser(final SessionInputBuffer buffer, final LineParser lineParser, final HttpRequestFactory requestFactory, final MessageConstraints constraints) {
        super(buffer, lineParser, constraints);
        this.requestFactory = ((requestFactory != null) ? requestFactory : DefaultHttpRequestFactory.INSTANCE);
        this.lineBuf = new CharArrayBuffer(128);
    }
    
    public DefaultHttpRequestParser(final SessionInputBuffer buffer, final MessageConstraints constraints) {
        this(buffer, null, null, constraints);
    }
    
    public DefaultHttpRequestParser(final SessionInputBuffer buffer) {
        this(buffer, null, null, MessageConstraints.DEFAULT);
    }
    
    protected HttpRequest parseHead(final SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
        this.lineBuf.clear();
        final int i = sessionBuffer.readLine(this.lineBuf);
        if (i == -1) {
            throw new ConnectionClosedException("Client closed connection");
        }
        final ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
        final RequestLine requestline = this.lineParser.parseRequestLine(this.lineBuf, cursor);
        return this.requestFactory.newHttpRequest(requestline);
    }
}
