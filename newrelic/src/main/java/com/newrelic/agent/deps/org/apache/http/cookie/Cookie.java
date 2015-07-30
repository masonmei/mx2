// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.cookie;

import java.util.Date;

public interface Cookie
{
    String getName();
    
    String getValue();
    
    String getComment();
    
    String getCommentURL();
    
    Date getExpiryDate();
    
    boolean isPersistent();
    
    String getDomain();
    
    String getPath();
    
    int[] getPorts();
    
    boolean isSecure();
    
    int getVersion();
    
    boolean isExpired(Date p0);
}
