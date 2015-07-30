// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.methods;

import java.net.URI;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;

public interface HttpUriRequest extends HttpRequest
{
    String getMethod();
    
    URI getURI();
    
    void abort() throws UnsupportedOperationException;
    
    boolean isAborted();
}
