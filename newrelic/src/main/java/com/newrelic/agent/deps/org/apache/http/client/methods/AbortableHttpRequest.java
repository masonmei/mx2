// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.methods;

import com.newrelic.agent.deps.org.apache.http.conn.ConnectionReleaseTrigger;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.conn.ClientConnectionRequest;

@Deprecated
public interface AbortableHttpRequest
{
    void setConnectionRequest(ClientConnectionRequest p0) throws IOException;
    
    void setReleaseTrigger(ConnectionReleaseTrigger p0) throws IOException;
    
    void abort();
}
