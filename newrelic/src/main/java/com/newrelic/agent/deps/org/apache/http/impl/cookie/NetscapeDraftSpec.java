// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import com.newrelic.agent.deps.org.apache.http.message.BufferedHeader;
import java.util.ArrayList;
import com.newrelic.agent.deps.org.apache.http.HeaderElement;
import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;
import com.newrelic.agent.deps.org.apache.http.message.ParserCursor;
import com.newrelic.agent.deps.org.apache.http.FormattedHeader;
import com.newrelic.agent.deps.org.apache.http.cookie.MalformedCookieException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.cookie.Cookie;
import java.util.List;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieOrigin;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieAttributeHandler;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class NetscapeDraftSpec extends CookieSpecBase
{
    protected static final String EXPIRES_PATTERN = "EEE, dd-MMM-yy HH:mm:ss z";
    private final String[] datepatterns;
    
    public NetscapeDraftSpec(final String[] datepatterns) {
        if (datepatterns != null) {
            this.datepatterns = datepatterns.clone();
        }
        else {
            this.datepatterns = new String[] { "EEE, dd-MMM-yy HH:mm:ss z" };
        }
        this.registerAttribHandler("path", new BasicPathHandler());
        this.registerAttribHandler("domain", new NetscapeDomainHandler());
        this.registerAttribHandler("max-age", new BasicMaxAgeHandler());
        this.registerAttribHandler("secure", new BasicSecureHandler());
        this.registerAttribHandler("comment", new BasicCommentHandler());
        this.registerAttribHandler("expires", new BasicExpiresHandler(this.datepatterns));
    }
    
    public NetscapeDraftSpec() {
        this(null);
    }
    
    public List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        if (!header.getName().equalsIgnoreCase("Set-Cookie")) {
            throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
        }
        final NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
        CharArrayBuffer buffer;
        ParserCursor cursor;
        if (header instanceof FormattedHeader) {
            buffer = ((FormattedHeader)header).getBuffer();
            cursor = new ParserCursor(((FormattedHeader)header).getValuePos(), buffer.length());
        }
        else {
            final String s = header.getValue();
            if (s == null) {
                throw new MalformedCookieException("Header value is null");
            }
            buffer = new CharArrayBuffer(s.length());
            buffer.append(s);
            cursor = new ParserCursor(0, buffer.length());
        }
        return this.parse(new HeaderElement[] { parser.parseHeader(buffer, cursor) }, origin);
    }
    
    public List<Header> formatCookies(final List<Cookie> cookies) {
        Args.notEmpty(cookies, "List of cookies");
        final CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
        buffer.append("Cookie");
        buffer.append(": ");
        for (int i = 0; i < cookies.size(); ++i) {
            final Cookie cookie = cookies.get(i);
            if (i > 0) {
                buffer.append("; ");
            }
            buffer.append(cookie.getName());
            final String s = cookie.getValue();
            if (s != null) {
                buffer.append("=");
                buffer.append(s);
            }
        }
        final List<Header> headers = new ArrayList<Header>(1);
        headers.add(new BufferedHeader(buffer));
        return headers;
    }
    
    public int getVersion() {
        return 0;
    }
    
    public Header getVersionHeader() {
        return null;
    }
    
    public String toString() {
        return "netscape";
    }
}
