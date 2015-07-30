// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.protocol;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;

public interface HttpExpectationVerifier
{
    void verify(HttpRequest p0, HttpResponse p1, HttpContext p2) throws HttpException;
}
