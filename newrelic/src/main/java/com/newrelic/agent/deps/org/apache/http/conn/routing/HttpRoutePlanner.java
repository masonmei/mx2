// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.routing;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.HttpHost;

public interface HttpRoutePlanner
{
    HttpRoute determineRoute(HttpHost p0, HttpRequest p1, HttpContext p2) throws HttpException;
}
