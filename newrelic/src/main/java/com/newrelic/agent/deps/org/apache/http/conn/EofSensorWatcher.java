// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;

public interface EofSensorWatcher
{
    boolean eofDetected(InputStream p0) throws IOException;
    
    boolean streamClosed(InputStream p0) throws IOException;
    
    boolean streamAbort(InputStream p0) throws IOException;
}
