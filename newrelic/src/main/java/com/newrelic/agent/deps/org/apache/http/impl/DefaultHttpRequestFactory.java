// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl;

import com.newrelic.agent.deps.org.apache.http.MethodNotSupportedException;
import com.newrelic.agent.deps.org.apache.http.message.BasicHttpEntityEnclosingRequest;
import com.newrelic.agent.deps.org.apache.http.message.BasicHttpRequest;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.RequestLine;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpRequestFactory;

@Immutable
public class DefaultHttpRequestFactory implements HttpRequestFactory
{
    public static final DefaultHttpRequestFactory INSTANCE;
    private static final String[] RFC2616_COMMON_METHODS;
    private static final String[] RFC2616_ENTITY_ENC_METHODS;
    private static final String[] RFC2616_SPECIAL_METHODS;
    
    private static boolean isOneOf(final String[] methods, final String method) {
        for (final String method2 : methods) {
            if (method2.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
    
    public HttpRequest newHttpRequest(final RequestLine requestline) throws MethodNotSupportedException {
        Args.notNull(requestline, "Request line");
        final String method = requestline.getMethod();
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(requestline);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(requestline);
        }
        throw new MethodNotSupportedException(method + " method not supported");
    }
    
    public HttpRequest newHttpRequest(final String method, final String uri) throws MethodNotSupportedException {
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_COMMON_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_ENTITY_ENC_METHODS, method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        }
        if (isOneOf(DefaultHttpRequestFactory.RFC2616_SPECIAL_METHODS, method)) {
            return new BasicHttpRequest(method, uri);
        }
        throw new MethodNotSupportedException(method + " method not supported");
    }
    
    static {
        INSTANCE = new DefaultHttpRequestFactory();
        RFC2616_COMMON_METHODS = new String[] { "GET" };
        RFC2616_ENTITY_ENC_METHODS = new String[] { "POST", "PUT" };
        RFC2616_SPECIAL_METHODS = new String[] { "HEAD", "OPTIONS", "DELETE", "TRACE", "CONNECT" };
    }
}
