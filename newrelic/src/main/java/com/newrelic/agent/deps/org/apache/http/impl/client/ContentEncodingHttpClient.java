// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.HttpResponseInterceptor;
import com.newrelic.agent.deps.org.apache.http.client.protocol.ResponseContentEncoding;
import com.newrelic.agent.deps.org.apache.http.HttpRequestInterceptor;
import com.newrelic.agent.deps.org.apache.http.client.protocol.RequestAcceptEncoding;
import com.newrelic.agent.deps.org.apache.http.protocol.BasicHttpProcessor;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.conn.ClientConnectionManager;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;

@Deprecated
@ThreadSafe
public class ContentEncodingHttpClient extends DefaultHttpClient
{
    public ContentEncodingHttpClient(final ClientConnectionManager conman, final HttpParams params) {
        super(conman, params);
    }
    
    public ContentEncodingHttpClient(final HttpParams params) {
        this(null, params);
    }
    
    public ContentEncodingHttpClient() {
        this((HttpParams)null);
    }
    
    protected BasicHttpProcessor createHttpProcessor() {
        final BasicHttpProcessor result = super.createHttpProcessor();
        result.addRequestInterceptor(new RequestAcceptEncoding());
        result.addResponseInterceptor(new ResponseContentEncoding());
        return result;
    }
}
