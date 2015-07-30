// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import com.newrelic.agent.deps.org.apache.http.HttpMessage;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.message.LineFormatter;
import com.newrelic.agent.deps.org.apache.http.io.SessionOutputBuffer;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;

@NotThreadSafe
public class DefaultHttpRequestWriter extends AbstractMessageWriter<HttpRequest>
{
    public DefaultHttpRequestWriter(final SessionOutputBuffer buffer, final LineFormatter formatter) {
        super(buffer, formatter);
    }
    
    public DefaultHttpRequestWriter(final SessionOutputBuffer buffer) {
        this(buffer, null);
    }
    
    protected void writeHeadLine(final HttpRequest message) throws IOException {
        this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
