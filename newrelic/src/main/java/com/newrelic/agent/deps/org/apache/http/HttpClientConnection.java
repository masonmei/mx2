// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.io.IOException;

public interface HttpClientConnection extends HttpConnection
{
    boolean isResponseAvailable(int p0) throws IOException;
    
    void sendRequestHeader(HttpRequest p0) throws HttpException, IOException;
    
    void sendRequestEntity(HttpEntityEnclosingRequest p0) throws HttpException, IOException;
    
    HttpResponse receiveResponseHeader() throws HttpException, IOException;
    
    void receiveResponseEntity(HttpResponse p0) throws HttpException, IOException;
    
    void flush() throws IOException;
}
