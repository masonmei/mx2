// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import com.newrelic.agent.deps.org.apache.http.HttpMessage;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.message.LineFormatter;
import com.newrelic.agent.deps.org.apache.http.io.SessionOutputBuffer;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;

@Deprecated
@NotThreadSafe
public class HttpResponseWriter extends AbstractMessageWriter<HttpResponse>
{
    public HttpResponseWriter(final SessionOutputBuffer buffer, final LineFormatter formatter, final HttpParams params) {
        super(buffer, formatter, params);
    }
    
    protected void writeHeadLine(final HttpResponse message) throws IOException {
        this.lineFormatter.formatStatusLine(this.lineBuf, message.getStatusLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
