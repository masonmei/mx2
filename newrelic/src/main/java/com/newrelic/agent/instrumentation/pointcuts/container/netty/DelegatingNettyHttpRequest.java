// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.netty;

import com.newrelic.agent.util.IteratorEnumeration;
import java.util.Enumeration;
import java.util.Iterator;
import com.newrelic.agent.Agent;
import java.util.Collection;
import java.util.LinkedList;
import java.net.HttpCookie;
import java.util.HashMap;
import com.newrelic.api.agent.HeaderType;
import java.util.List;
import java.util.Map;
import com.newrelic.api.agent.Request;

public class DelegatingNettyHttpRequest implements Request
{
    public static final String COOKIE_HEADER_NAME = "Cookie";
    private final NettyHttpRequest delegate;
    private volatile Map<String, String> cookies;
    private volatile Map<String, List<Object>> parameters;
    
    private DelegatingNettyHttpRequest(final NettyHttpRequest delegate) {
        this.delegate = delegate;
    }
    
    public HeaderType getHeaderType() {
        return HeaderType.HTTP;
    }
    
    private Map<String, String> getCookies() {
        if (this.cookies == null) {
            this.cookies = new HashMap<String, String>();
            final List<String> cookieHeaders = this.delegate.getHeaders("Cookie");
            for (final String cookieHeader : cookieHeaders) {
                List<HttpCookie> httpCookies;
                try {
                    httpCookies = HttpCookie.parse(cookieHeader);
                }
                catch (IllegalArgumentException e) {
                    httpCookies = new LinkedList<HttpCookie>();
                    for (final String part : cookieHeader.split(";")) {
                        try {
                            httpCookies.addAll(HttpCookie.parse(part));
                        }
                        catch (IllegalArgumentException e2) {
                            Agent.LOG.fine("Failed to parse Cookie part: " + part);
                        }
                    }
                }
                for (final HttpCookie httpCookie : httpCookies) {
                    this.cookies.put(httpCookie.getName(), httpCookie.getValue());
                }
            }
        }
        return this.cookies;
    }
    
    public void setParameters(final Map<String, List<Object>> params) {
        this.parameters = params;
    }
    
    public Enumeration<?> getParameterNames() {
        if (this.parameters == null) {
            return null;
        }
        final Iterator<String> it = this.parameters.keySet().iterator();
        return new IteratorEnumeration<Object>(it);
    }
    
    public String[] getParameterValues(final String name) {
        if (this.parameters == null) {
            return null;
        }
        return (String[])((this.parameters.get(name) == null) ? null : ((String[])this.parameters.get(name).toArray(new String[0])));
    }
    
    public Object getAttribute(final String name) {
        return null;
    }
    
    public String getRequestURI() {
        return this.delegate.getUri();
    }
    
    public String getHeader(final String name) {
        final List<String> nameHeaders = this.delegate.getHeaders(name);
        if (nameHeaders != null && nameHeaders.size() > 0) {
            return nameHeaders.get(0);
        }
        return null;
    }
    
    public String getRemoteUser() {
        return null;
    }
    
    public String getCookieValue(final String name) {
        final Map<String, String> map = this.getCookies();
        if (map == null) {
            return null;
        }
        return map.get(name);
    }
    
    static Request create(final NettyHttpRequest delegate) {
        return (Request)new DelegatingNettyHttpRequest(delegate);
    }
}
