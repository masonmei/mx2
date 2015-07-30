// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;

@Deprecated
public interface HostNameResolver
{
    InetAddress resolve(String p0) throws IOException;
}
