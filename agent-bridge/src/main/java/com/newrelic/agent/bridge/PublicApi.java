// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import java.util.Map;

public interface PublicApi
{
    void noticeError(Throwable p0, Map<String, String> p1);
    
    void noticeError(Throwable p0);
    
    void noticeError(String p0, Map<String, String> p1);
    
    void noticeError(String p0);
    
    void addCustomParameter(String p0, Number p1);
    
    void addCustomParameter(String p0, String p1);
    
    void setTransactionName(String p0, String p1);
    
    void ignoreTransaction();
    
    void ignoreApdex();
    
    void setRequestAndResponse(Request p0, Response p1);
    
    String getBrowserTimingHeader();
    
    String getBrowserTimingFooter();
    
    void setUserName(String p0);
    
    void setAccountName(String p0);
    
    void setProductName(String p0);
}
