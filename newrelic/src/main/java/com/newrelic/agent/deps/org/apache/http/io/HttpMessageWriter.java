// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.io;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpMessage;

public interface HttpMessageWriter<T extends HttpMessage>
{
    void write(T p0) throws IOException, HttpException;
}
