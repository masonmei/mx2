// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.browser;

import java.util.Map;

public interface BrowserTransactionState
{
    long getDurationInMilliseconds();
    
    long getExternalTimeInMilliseconds();
    
    String getBrowserTimingHeader();
    
    String getBrowserTimingHeaderForJsp();
    
    String getBrowserTimingFooter();
    
    String getTransactionName();
    
    Map<String, Object> getUserAttributes();
    
    Map<String, Object> getAgentAttributes();
    
    String getAppName();
}
