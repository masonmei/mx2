// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.client.methods.RequestBuilder;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpGet;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpHead;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpUriRequest;
import com.newrelic.agent.deps.org.apache.http.util.TextUtils;
import java.util.Locale;
import com.newrelic.agent.deps.org.apache.http.client.utils.URIBuilder;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.http.client.config.RequestConfig;
import com.newrelic.agent.deps.org.apache.http.client.CircularRedirectException;
import java.net.URISyntaxException;
import com.newrelic.agent.deps.org.apache.http.client.utils.URIUtils;
import com.newrelic.agent.deps.org.apache.http.util.Asserts;
import com.newrelic.agent.deps.org.apache.http.client.protocol.HttpClientContext;
import java.net.URI;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.commons.logging.LogFactory;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.client.RedirectStrategy;

@Immutable
public class DefaultRedirectStrategy implements RedirectStrategy
{
    private final Log log;
    @Deprecated
    public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    public static final DefaultRedirectStrategy INSTANCE;
    private static final String[] REDIRECT_METHODS;
    
    public DefaultRedirectStrategy() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        Args.notNull(request, "HTTP request");
        Args.notNull(response, "HTTP response");
        final int statusCode = response.getStatusLine().getStatusCode();
        final String method = request.getRequestLine().getMethod();
        final Header locationHeader = response.getFirstHeader("location");
        switch (statusCode) {
            case 302: {
                return this.isRedirectable(method) && locationHeader != null;
            }
            case 301:
            case 307: {
                return this.isRedirectable(method);
            }
            case 303: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public URI getLocationURI(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        Args.notNull(request, "HTTP request");
        Args.notNull(response, "HTTP response");
        Args.notNull(context, "HTTP context");
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final Header locationHeader = response.getFirstHeader("location");
        if (locationHeader == null) {
            throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
        }
        final String location = locationHeader.getValue();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Redirect requested to location '" + location + "'");
        }
        final RequestConfig config = clientContext.getRequestConfig();
        URI uri = this.createLocationURI(location);
        try {
            if (!uri.isAbsolute()) {
                if (!config.isRelativeRedirectsAllowed()) {
                    throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
                }
                final HttpHost target = clientContext.getTargetHost();
                Asserts.notNull(target, "Target host");
                final URI requestURI = new URI(request.getRequestLine().getUri());
                final URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, false);
                uri = URIUtils.resolve(absoluteRequestURI, uri);
            }
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException(ex.getMessage(), ex);
        }
        RedirectLocations redirectLocations = (RedirectLocations)clientContext.getAttribute("http.protocol.redirect-locations");
        if (redirectLocations == null) {
            redirectLocations = new RedirectLocations();
            context.setAttribute("http.protocol.redirect-locations", redirectLocations);
        }
        if (!config.isCircularRedirectsAllowed() && redirectLocations.contains(uri)) {
            throw new CircularRedirectException("Circular redirect to '" + uri + "'");
        }
        redirectLocations.add(uri);
        return uri;
    }
    
    protected URI createLocationURI(final String location) throws ProtocolException {
        try {
            final URIBuilder b = new URIBuilder(new URI(location).normalize());
            final String host = b.getHost();
            if (host != null) {
                b.setHost(host.toLowerCase(Locale.US));
            }
            final String path = b.getPath();
            if (TextUtils.isEmpty(path)) {
                b.setPath("/");
            }
            return b.build();
        }
        catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid redirect URI: " + location, ex);
        }
    }
    
    protected boolean isRedirectable(final String method) {
        for (final String m : DefaultRedirectStrategy.REDIRECT_METHODS) {
            if (m.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
    
    public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        final URI uri = this.getLocationURI(request, response, context);
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("HEAD")) {
            return new HttpHead(uri);
        }
        if (method.equalsIgnoreCase("GET")) {
            return new HttpGet(uri);
        }
        final int status = response.getStatusLine().getStatusCode();
        if (status == 307) {
            return RequestBuilder.copy(request).setUri(uri).build();
        }
        return new HttpGet(uri);
    }
    
    static {
        INSTANCE = new DefaultRedirectStrategy();
        REDIRECT_METHODS = new String[] { "GET", "HEAD" };
    }
}
