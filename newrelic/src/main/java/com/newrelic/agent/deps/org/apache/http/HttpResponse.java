// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.util.Locale;

public interface HttpResponse extends HttpMessage
{
    StatusLine getStatusLine();
    
    void setStatusLine(StatusLine p0);
    
    void setStatusLine(ProtocolVersion p0, int p1);
    
    void setStatusLine(ProtocolVersion p0, int p1, String p2);
    
    void setStatusCode(int p0) throws IllegalStateException;
    
    void setReasonPhrase(String p0) throws IllegalStateException;
    
    HttpEntity getEntity();
    
    void setEntity(HttpEntity p0);
    
    @Deprecated
    Locale getLocale();
    
    @Deprecated
    void setLocale(Locale p0);
}
