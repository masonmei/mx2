// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.client.protocol.RequestProxyAuthentication;
import com.newrelic.agent.deps.org.apache.http.client.protocol.RequestTargetAuthentication;
import com.newrelic.agent.deps.org.apache.http.client.protocol.RequestAuthCache;
import com.newrelic.agent.deps.org.apache.http.HttpResponseInterceptor;
import com.newrelic.agent.deps.org.apache.http.client.protocol.ResponseProcessCookies;
import com.newrelic.agent.deps.org.apache.http.client.protocol.RequestAddCookies;
import com.newrelic.agent.deps.org.apache.http.protocol.RequestExpectContinue;
import com.newrelic.agent.deps.org.apache.http.protocol.RequestUserAgent;
import com.newrelic.agent.deps.org.apache.http.client.protocol.RequestClientConnControl;
import com.newrelic.agent.deps.org.apache.http.protocol.RequestTargetHost;
import com.newrelic.agent.deps.org.apache.http.protocol.RequestContent;
import com.newrelic.agent.deps.org.apache.http.HttpRequestInterceptor;
import com.newrelic.agent.deps.org.apache.http.client.protocol.RequestDefaultHeaders;
import com.newrelic.agent.deps.org.apache.http.protocol.BasicHttpProcessor;
import com.newrelic.agent.deps.org.apache.http.params.HttpConnectionParams;
import com.newrelic.agent.deps.org.apache.http.protocol.HTTP;
import com.newrelic.agent.deps.org.apache.http.ProtocolVersion;
import com.newrelic.agent.deps.org.apache.http.params.HttpProtocolParams;
import com.newrelic.agent.deps.org.apache.http.HttpVersion;
import com.newrelic.agent.deps.org.apache.http.params.SyncBasicHttpParams;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.conn.ClientConnectionManager;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;

@Deprecated
@ThreadSafe
public class DefaultHttpClient extends AbstractHttpClient
{
    public DefaultHttpClient(final ClientConnectionManager conman, final HttpParams params) {
        super(conman, params);
    }
    
    public DefaultHttpClient(final ClientConnectionManager conman) {
        super(conman, null);
    }
    
    public DefaultHttpClient(final HttpParams params) {
        super(null, params);
    }
    
    public DefaultHttpClient() {
        super(null, null);
    }
    
    protected HttpParams createHttpParams() {
        final HttpParams params = new SyncBasicHttpParams();
        setDefaultHttpParams(params);
        return params;
    }
    
    public static void setDefaultHttpParams(final HttpParams params) {
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEF_CONTENT_CHARSET.name());
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpProtocolParams.setUserAgent(params, HttpClientBuilder.DEFAULT_USER_AGENT);
    }
    
    protected BasicHttpProcessor createHttpProcessor() {
        final BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new RequestDefaultHeaders());
        httpproc.addInterceptor(new RequestContent());
        httpproc.addInterceptor(new RequestTargetHost());
        httpproc.addInterceptor(new RequestClientConnControl());
        httpproc.addInterceptor(new RequestUserAgent());
        httpproc.addInterceptor(new RequestExpectContinue());
        httpproc.addInterceptor(new RequestAddCookies());
        httpproc.addInterceptor(new ResponseProcessCookies());
        httpproc.addInterceptor(new RequestAuthCache());
        httpproc.addInterceptor(new RequestTargetAuthentication());
        httpproc.addInterceptor(new RequestProxyAuthentication());
        return httpproc;
    }
}
