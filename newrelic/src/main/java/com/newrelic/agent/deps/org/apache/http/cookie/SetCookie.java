// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.cookie;

import java.util.Date;

public interface SetCookie extends Cookie
{
    void setValue(String p0);
    
    void setComment(String p0);
    
    void setExpiryDate(Date p0);
    
    void setDomain(String p0);
    
    void setPath(String p0);
    
    void setSecure(boolean p0);
    
    void setVersion(int p0);
}
