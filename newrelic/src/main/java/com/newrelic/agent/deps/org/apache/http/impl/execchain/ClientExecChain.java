// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.execchain;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.client.methods.CloseableHttpResponse;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpExecutionAware;
import com.newrelic.agent.deps.org.apache.http.client.protocol.HttpClientContext;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpRequestWrapper;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;

public interface ClientExecChain
{
    CloseableHttpResponse execute(HttpRoute p0, HttpRequestWrapper p1, HttpClientContext p2, HttpExecutionAware p3) throws IOException, HttpException;
}
