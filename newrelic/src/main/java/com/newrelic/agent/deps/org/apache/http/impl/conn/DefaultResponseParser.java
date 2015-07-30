// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.StatusLine;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.message.ParserCursor;
import com.newrelic.agent.deps.org.apache.http.NoHttpResponseException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.commons.logging.LogFactory;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.message.LineParser;
import com.newrelic.agent.deps.org.apache.http.io.SessionInputBuffer;
import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;
import com.newrelic.agent.deps.org.apache.http.HttpResponseFactory;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpMessage;
import com.newrelic.agent.deps.org.apache.http.impl.io.AbstractMessageParser;

@Deprecated
@ThreadSafe
public class DefaultResponseParser extends AbstractMessageParser<HttpMessage>
{
    private final Log log;
    private final HttpResponseFactory responseFactory;
    private final CharArrayBuffer lineBuf;
    private final int maxGarbageLines;
    
    public DefaultResponseParser(final SessionInputBuffer buffer, final LineParser parser, final HttpResponseFactory responseFactory, final HttpParams params) {
        super(buffer, parser, params);
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(responseFactory, "Response factory");
        this.responseFactory = responseFactory;
        this.lineBuf = new CharArrayBuffer(128);
        this.maxGarbageLines = this.getMaxGarbageLines(params);
    }
    
    protected int getMaxGarbageLines(final HttpParams params) {
        return params.getIntParameter("http.connection.max-status-line-garbage", Integer.MAX_VALUE);
    }
    
    protected HttpMessage parseHead(final SessionInputBuffer sessionBuffer) throws IOException, HttpException {
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
            if (i == -1 || count >= this.maxGarbageLines) {
                throw new ProtocolException("The server failed to respond with a valid HTTP response");
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Garbage in response: " + this.lineBuf.toString());
            }
            ++count;
        }
    }
}
