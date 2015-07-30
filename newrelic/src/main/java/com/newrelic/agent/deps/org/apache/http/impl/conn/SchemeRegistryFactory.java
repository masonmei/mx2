// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import com.newrelic.agent.deps.org.apache.http.conn.ssl.SSLSocketFactory;
import com.newrelic.agent.deps.org.apache.http.conn.scheme.SchemeSocketFactory;
import com.newrelic.agent.deps.org.apache.http.conn.scheme.Scheme;
import com.newrelic.agent.deps.org.apache.http.conn.scheme.PlainSocketFactory;
import com.newrelic.agent.deps.org.apache.http.conn.scheme.SchemeRegistry;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;

@Deprecated
@ThreadSafe
public final class SchemeRegistryFactory
{
    public static SchemeRegistry createDefault() {
        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        return registry;
    }
    
    public static SchemeRegistry createSystemDefault() {
        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, SSLSocketFactory.getSystemSocketFactory()));
        return registry;
    }
}
