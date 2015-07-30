// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.dispatchers;

import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.config.TransactionTracerConfig;

public interface Dispatcher
{
    void setTransactionName();
    
    String getUri();
    
    TransactionTracerConfig getTransactionTracerConfig();
    
    boolean isWebTransaction();
    
    boolean isAsyncTransaction();
    
    void transactionFinished(String p0, TransactionStats p1);
    
    String getCookieValue(String p0);
    
    String getHeader(String p0);
    
    Request getRequest();
    
    void setRequest(Request p0);
    
    Response getResponse();
    
    void setResponse(Response p0);
    
    void setIgnoreApdex(boolean p0);
    
    boolean isIgnoreApdex();
}
