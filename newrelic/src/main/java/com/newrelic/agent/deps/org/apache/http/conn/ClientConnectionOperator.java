// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import java.net.InetAddress;
import com.newrelic.agent.deps.org.apache.http.HttpHost;

@Deprecated
public interface ClientConnectionOperator
{
    OperatedClientConnection createConnection();
    
    void openConnection(OperatedClientConnection p0, HttpHost p1, InetAddress p2, HttpContext p3, HttpParams p4) throws IOException;
    
    void updateSecureConnection(OperatedClientConnection p0, HttpHost p1, HttpContext p2, HttpParams p3) throws IOException;
}
