// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.helpers;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import com.newrelic.agent.deps.org.slf4j.MDC;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.Filter;

public class MDCInsertingServletFilter implements Filter
{
    public void destroy() {
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        this.insertIntoMDC(request);
        try {
            chain.doFilter(request, response);
        }
        finally {
            this.clearMDC();
        }
    }
    
    void insertIntoMDC(final ServletRequest request) {
        MDC.put("req.remoteHost", request.getRemoteHost());
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest)request;
            MDC.put("req.requestURI", httpServletRequest.getRequestURI());
            final StringBuffer requestURL = httpServletRequest.getRequestURL();
            if (requestURL != null) {
                MDC.put("req.requestURL", requestURL.toString());
            }
            MDC.put("req.queryString", httpServletRequest.getQueryString());
            MDC.put("req.userAgent", httpServletRequest.getHeader("User-Agent"));
            MDC.put("req.xForwardedFor", httpServletRequest.getHeader("X-Forwarded-For"));
        }
    }
    
    void clearMDC() {
        MDC.remove("req.remoteHost");
        MDC.remove("req.requestURI");
        MDC.remove("req.queryString");
        MDC.remove("req.requestURL");
        MDC.remove("req.userAgent");
        MDC.remove("req.xForwardedFor");
    }
    
    public void init(final FilterConfig arg0) throws ServletException {
    }
}
