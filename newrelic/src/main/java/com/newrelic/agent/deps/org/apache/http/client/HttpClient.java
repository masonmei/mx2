// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpUriRequest;
import com.newrelic.agent.deps.org.apache.http.conn.ClientConnectionManager;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;

public interface HttpClient
{
    @Deprecated
    HttpParams getParams();
    
    @Deprecated
    ClientConnectionManager getConnectionManager();
    
    HttpResponse execute(HttpUriRequest p0) throws IOException, ClientProtocolException;
    
    HttpResponse execute(HttpUriRequest p0, HttpContext p1) throws IOException, ClientProtocolException;
    
    HttpResponse execute(HttpHost p0, HttpRequest p1) throws IOException, ClientProtocolException;
    
    HttpResponse execute(HttpHost p0, HttpRequest p1, HttpContext p2) throws IOException, ClientProtocolException;
    
     <T> T execute(HttpUriRequest p0, ResponseHandler<? extends T> p1) throws IOException, ClientProtocolException;
    
     <T> T execute(HttpUriRequest p0, ResponseHandler<? extends T> p1, HttpContext p2) throws IOException, ClientProtocolException;
    
     <T> T execute(HttpHost p0, HttpRequest p1, ResponseHandler<? extends T> p2) throws IOException, ClientProtocolException;
    
     <T> T execute(HttpHost p0, HttpRequest p1, ResponseHandler<? extends T> p2, HttpContext p3) throws IOException, ClientProtocolException;
}
