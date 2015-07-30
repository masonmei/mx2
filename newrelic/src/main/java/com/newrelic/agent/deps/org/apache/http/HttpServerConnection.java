// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.io.IOException;

public interface HttpServerConnection extends HttpConnection
{
    HttpRequest receiveRequestHeader() throws HttpException, IOException;
    
    void receiveRequestEntity(HttpEntityEnclosingRequest p0) throws HttpException, IOException;
    
    void sendResponseHeader(HttpResponse p0) throws HttpException, IOException;
    
    void sendResponseEntity(HttpResponse p0) throws HttpException, IOException;
    
    void flush() throws IOException;
}
