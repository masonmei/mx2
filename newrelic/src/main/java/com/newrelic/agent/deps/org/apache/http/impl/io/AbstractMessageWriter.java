// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.HeaderIterator;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.message.BasicLineFormatter;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.message.LineFormatter;
import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;
import com.newrelic.agent.deps.org.apache.http.io.SessionOutputBuffer;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.io.HttpMessageWriter;
import com.newrelic.agent.deps.org.apache.http.HttpMessage;

@NotThreadSafe
public abstract class AbstractMessageWriter<T extends HttpMessage> implements HttpMessageWriter<T>
{
    protected final SessionOutputBuffer sessionBuffer;
    protected final CharArrayBuffer lineBuf;
    protected final LineFormatter lineFormatter;
    
    public AbstractMessageWriter(final SessionOutputBuffer buffer, final LineFormatter formatter, final HttpParams params) {
        Args.notNull(buffer, "Session input buffer");
        this.sessionBuffer = buffer;
        this.lineBuf = new CharArrayBuffer(128);
        this.lineFormatter = ((formatter != null) ? formatter : BasicLineFormatter.INSTANCE);
    }
    
    public AbstractMessageWriter(final SessionOutputBuffer buffer, final LineFormatter formatter) {
        this.sessionBuffer = Args.notNull(buffer, "Session input buffer");
        this.lineFormatter = ((formatter != null) ? formatter : BasicLineFormatter.INSTANCE);
        this.lineBuf = new CharArrayBuffer(128);
    }
    
    protected abstract void writeHeadLine(final T p0) throws IOException;
    
    public void write(final T message) throws IOException, HttpException {
        Args.notNull(message, "HTTP message");
        this.writeHeadLine(message);
        final HeaderIterator it = message.headerIterator();
        while (it.hasNext()) {
            final Header header = it.nextHeader();
            this.sessionBuffer.writeLine(this.lineFormatter.formatHeader(this.lineBuf, header));
        }
        this.lineBuf.clear();
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
