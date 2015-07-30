// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.HttpHost;

@Deprecated
public interface RequestDirector
{
    HttpResponse execute(HttpHost p0, HttpRequest p1, HttpContext p2) throws HttpException, IOException;
}
