// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.io;

import com.newrelic.agent.deps.org.apache.http.HttpMessage;

public interface HttpMessageWriterFactory<T extends HttpMessage>
{
    HttpMessageWriter<T> create(SessionOutputBuffer p0);
}
