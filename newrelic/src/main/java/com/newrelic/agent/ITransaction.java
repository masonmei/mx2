// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.util.Map;
import com.newrelic.agent.browser.BrowserTransactionState;
import com.newrelic.agent.config.CrossProcessConfig;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.transaction.PriorityTransactionName;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.dispatchers.Dispatcher;

public interface ITransaction
{
    Dispatcher getDispatcher();
    
    Tracer getRootTracer();
    
    boolean isStarted();
    
    boolean isFinished();
    
    boolean isInProgress();
    
    boolean isIgnore();
    
    String getApplicationName();
    
    PriorityTransactionName getPriorityTransactionName();
    
    long getExternalTime();
    
    String getGuid();
    
    long getRunningDurationInNanos();
    
    AgentConfig getAgentConfig();
    
    CrossProcessConfig getCrossProcessConfig();
    
    void freezeTransactionName();
    
    BrowserTransactionState getBrowserTransactionState();
    
    InboundHeaderState getInboundHeaderState();
    
    CrossProcessTransactionState getCrossProcessTransactionState();
    
    TransactionActivity getTransactionActivity();
    
    Map<String, Object> getUserAttributes();
    
    Map<String, Map<String, String>> getPrefixedAgentAttributes();
    
    Map<String, Object> getAgentAttributes();
    
    Map<String, Object> getIntrinsicAttributes();
}
