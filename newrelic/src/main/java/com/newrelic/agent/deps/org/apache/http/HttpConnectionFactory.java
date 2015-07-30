// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.io.IOException;
import java.net.Socket;

public interface HttpConnectionFactory<T extends HttpConnection>
{
    T createConnection(Socket p0) throws IOException;
}
