// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import com.newrelic.api.agent.ApplicationNamePriority;
import java.util.Map;

public interface Transaction extends com.newrelic.api.agent.Transaction
{
    Map<String, Object> getAgentAttributes();
    
    boolean setTransactionName(TransactionNamePriority p0, boolean p1, String p2, String... p3);
    
    void beforeSendResponseHeaders();
    
    boolean isStarted();
    
    void setApplicationName(ApplicationNamePriority p0, String p1);
    
    boolean isAutoAppNamingEnabled();
    
    boolean isWebRequestSet();
    
    boolean isWebResponseSet();
    
    void setWebRequest(Request p0);
    
    void setWebResponse(Response p0);
    
    void provideHeaders(InboundHeaders p0);
    
    WebResponse getWebResponse();
    
    void convertToWebTransaction();
    
    boolean isWebTransaction();
    
    void requestInitialized(Request p0, Response p1);
    
    void requestDestroyed();
    
    void saveMessageParameters(Map<String, String> p0);
    
    CrossProcessState getCrossProcessState();
    
    TracedMethod startFlyweightTracer();
    
    void finishFlyweightTracer(TracedMethod p0, long p1, long p2, String p3, String p4, String p5, String p6, String[] p7);
    
    boolean registerAsyncActivity(Object p0);
    
    boolean startAsyncActivity(Object p0);
    
    boolean ignoreAsyncActivity(Object p0);
}
