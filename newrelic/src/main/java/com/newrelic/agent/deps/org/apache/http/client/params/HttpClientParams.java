// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.params;

import com.newrelic.agent.deps.org.apache.http.params.HttpConnectionParams;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Deprecated
@Immutable
public class HttpClientParams
{
    public static boolean isRedirecting(final HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        return params.getBooleanParameter("http.protocol.handle-redirects", true);
    }
    
    public static void setRedirecting(final HttpParams params, final boolean value) {
        Args.notNull(params, "HTTP parameters");
        params.setBooleanParameter("http.protocol.handle-redirects", value);
    }
    
    public static boolean isAuthenticating(final HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        return params.getBooleanParameter("http.protocol.handle-authentication", true);
    }
    
    public static void setAuthenticating(final HttpParams params, final boolean value) {
        Args.notNull(params, "HTTP parameters");
        params.setBooleanParameter("http.protocol.handle-authentication", value);
    }
    
    public static String getCookiePolicy(final HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        final String cookiePolicy = (String)params.getParameter("http.protocol.cookie-policy");
        if (cookiePolicy == null) {
            return "best-match";
        }
        return cookiePolicy;
    }
    
    public static void setCookiePolicy(final HttpParams params, final String cookiePolicy) {
        Args.notNull(params, "HTTP parameters");
        params.setParameter("http.protocol.cookie-policy", cookiePolicy);
    }
    
    public static void setConnectionManagerTimeout(final HttpParams params, final long timeout) {
        Args.notNull(params, "HTTP parameters");
        params.setLongParameter("http.conn-manager.timeout", timeout);
    }
    
    public static long getConnectionManagerTimeout(final HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        final Long timeout = (Long)params.getParameter("http.conn-manager.timeout");
        if (timeout != null) {
            return timeout;
        }
        return HttpConnectionParams.getConnectionTimeout(params);
    }
}
