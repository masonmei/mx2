// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import java.net.UnknownHostException;
import java.net.InetAddress;

public interface DnsResolver
{
    InetAddress[] resolve(String p0) throws UnknownHostException;
}
