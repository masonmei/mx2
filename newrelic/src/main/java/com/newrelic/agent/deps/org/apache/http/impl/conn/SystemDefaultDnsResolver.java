// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import java.net.UnknownHostException;
import java.net.InetAddress;
import com.newrelic.agent.deps.org.apache.http.conn.DnsResolver;

public class SystemDefaultDnsResolver implements DnsResolver
{
    public static final SystemDefaultDnsResolver INSTANCE;
    
    public InetAddress[] resolve(final String host) throws UnknownHostException {
        return InetAddress.getAllByName(host);
    }
    
    static {
        INSTANCE = new SystemDefaultDnsResolver();
    }
}