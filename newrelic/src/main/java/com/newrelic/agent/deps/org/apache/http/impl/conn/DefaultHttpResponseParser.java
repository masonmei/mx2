// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import com.newrelic.agent.deps.org.apache.http.ParseException;
import com.newrelic.agent.deps.org.apache.http.HttpMessage;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.StatusLine;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.message.ParserCursor;
import com.newrelic.agent.deps.org.apache.http.NoHttpResponseException;
import com.newrelic.agent.deps.org.apache.http.impl.DefaultHttpResponseFactory;
import com.newrelic.agent.deps.org.apache.http.config.MessageConstraints;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.commons.logging.LogFactory;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.message.LineParser;
import com.newrelic.agent.deps.org.apache.http.io.SessionInputBuffer;
import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;
import com.newrelic.agent.deps.org.apache.http.HttpResponseFactory;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.impl.io.AbstractMessageParser;

@NotThreadSafe
public class DefaultHttpResponseParser extends AbstractMessageParser<HttpResponse>
{
    private final Log log;
    private final HttpResponseFactory responseFactory;
    private final CharArrayBuffer lineBuf;
    
    public DefaultHttpResponseParser(final SessionInputBuffer buffer, final LineParser parser, final HttpResponseFactory responseFactory, final HttpParams params) {
        super(buffer, parser, params);
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(responseFactory, "Response factory");
        this.responseFactory = responseFactory;
        this.lineBuf = new CharArrayBuffer(128);
    }
    
    public DefaultHttpResponseParser(final SessionInputBuffer buffer, final LineParser lineParser, final HttpResponseFactory responseFactory, final MessageConstraints constraints) {
        super(buffer, lineParser, constraints);
        this.log = LogFactory.getLog(this.getClass());
        this.responseFactory = ((responseFactory != null) ? responseFactory : DefaultHttpResponseFactory.INSTANCE);
        this.lineBuf = new CharArrayBuffer(128);
    }
    
    public DefaultHttpResponseParser(final SessionInputBuffer buffer, final MessageConstraints constraints) {
        this(buffer, null, null, constraints);
    }
    
    public DefaultHttpResponseParser(final SessionInputBuffer buffer) {
        this(buffer, null, null, MessageConstraints.DEFAULT);
    }
    
    protected HttpResponse parseHead(final SessionInputBuffer sessionBuffer) throws IOException, HttpException {
        int count = 0;
        ParserCursor cursor = null;
        while (true) {
            this.lineBuf.clear();
            final int i = sessionBuffer.readLine(this.lineBuf);
            if (i == -1 && count == 0) {
                throw new NoHttpResponseException("The target server failed to respond");
            }
            cursor = new ParserCursor(0, this.lineBuf.length());
            if (this.lineParser.hasProtocolVersion(this.lineBuf, cursor)) {
                final StatusLine statusline = this.lineParser.parseStatusLine(this.lineBuf, cursor);
                return this.responseFactory.newHttpResponse(statusline, null);
            }
            if (i == -1 || this.reject(this.lineBuf, count)) {
                throw new ProtocolException("The server failed to respond with a valid HTTP response");
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Garbage in response: " + this.lineBuf.toString());
            }
            ++count;
        }
    }
    
    protected boolean reject(final CharArrayBuffer line, final int count) {
        return false;
    }
}
