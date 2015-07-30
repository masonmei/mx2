// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl;

import com.newrelic.agent.deps.org.apache.http.StatusLine;
import java.util.Locale;
import com.newrelic.agent.deps.org.apache.http.message.BasicHttpResponse;
import com.newrelic.agent.deps.org.apache.http.message.BasicStatusLine;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.ProtocolVersion;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.ReasonPhraseCatalog;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpResponseFactory;

@Immutable
public class DefaultHttpResponseFactory implements HttpResponseFactory
{
    public static final DefaultHttpResponseFactory INSTANCE;
    protected final ReasonPhraseCatalog reasonCatalog;
    
    public DefaultHttpResponseFactory(final ReasonPhraseCatalog catalog) {
        this.reasonCatalog = Args.notNull(catalog, "Reason phrase catalog");
    }
    
    public DefaultHttpResponseFactory() {
        this(EnglishReasonPhraseCatalog.INSTANCE);
    }
    
    public HttpResponse newHttpResponse(final ProtocolVersion ver, final int status, final HttpContext context) {
        Args.notNull(ver, "HTTP version");
        final Locale loc = this.determineLocale(context);
        final String reason = this.reasonCatalog.getReason(status, loc);
        final StatusLine statusline = new BasicStatusLine(ver, status, reason);
        return new BasicHttpResponse(statusline);
    }
    
    public HttpResponse newHttpResponse(final StatusLine statusline, final HttpContext context) {
        Args.notNull(statusline, "Status line");
        return new BasicHttpResponse(statusline);
    }
    
    protected Locale determineLocale(final HttpContext context) {
        return Locale.getDefault();
    }
    
    static {
        INSTANCE = new DefaultHttpResponseFactory();
    }
}
