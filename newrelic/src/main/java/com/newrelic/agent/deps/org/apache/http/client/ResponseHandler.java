// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;

public interface ResponseHandler<T>
{
    T handleResponse(HttpResponse p0) throws ClientProtocolException, IOException;
}
