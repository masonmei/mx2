// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import com.newrelic.agent.deps.org.apache.http.HttpMessage;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.message.LineFormatter;
import com.newrelic.agent.deps.org.apache.http.io.SessionOutputBuffer;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;

@NotThreadSafe
public class DefaultHttpResponseWriter extends AbstractMessageWriter<HttpResponse>
{
    public DefaultHttpResponseWriter(final SessionOutputBuffer buffer, final LineFormatter formatter) {
        super(buffer, formatter);
    }
    
    public DefaultHttpResponseWriter(final SessionOutputBuffer buffer) {
        super(buffer, null);
    }
    
    protected void writeHeadLine(final HttpResponse message) throws IOException {
        this.lineFormatter.formatStatusLine(this.lineBuf, message.getStatusLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
