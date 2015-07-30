// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.HeaderElement;
import com.newrelic.agent.deps.org.apache.http.HeaderElementIterator;
import com.newrelic.agent.deps.org.apache.http.message.BasicHeaderElementIterator;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.conn.ConnectionKeepAliveStrategy;

@Immutable
public class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy
{
    public static final DefaultConnectionKeepAliveStrategy INSTANCE;
    
    public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
        Args.notNull(response, "HTTP response");
        final HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
        while (it.hasNext()) {
            final HeaderElement he = it.nextElement();
            final String param = he.getName();
            final String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                    return Long.parseLong(value) * 1000L;
                }
                catch (NumberFormatException ex) {}
            }
        }
        return -1L;
    }
    
    static {
        INSTANCE = new DefaultConnectionKeepAliveStrategy();
    }
}
