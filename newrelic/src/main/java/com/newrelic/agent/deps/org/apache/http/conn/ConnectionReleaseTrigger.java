// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import java.io.IOException;

public interface ConnectionReleaseTrigger
{
    void releaseConnection() throws IOException;
    
    void abortConnection() throws IOException;
}
