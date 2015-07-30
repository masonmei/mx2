// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.methods;

import java.net.URI;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class HttpDelete extends HttpRequestBase
{
    public static final String METHOD_NAME = "DELETE";
    
    public HttpDelete() {
    }
    
    public HttpDelete(final URI uri) {
        this.setURI(uri);
    }
    
    public HttpDelete(final String uri) {
        this.setURI(URI.create(uri));
    }
    
    public String getMethod() {
        return "DELETE";
    }
}
