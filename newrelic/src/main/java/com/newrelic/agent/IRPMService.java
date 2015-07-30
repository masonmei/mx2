// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.service.analytics.CustomInsightsEvent;
import com.newrelic.agent.service.analytics.TransactionEvent;
import java.util.Collection;
import com.newrelic.agent.service.module.Jar;
import com.newrelic.agent.errors.ErrorService;
import com.newrelic.agent.trace.TransactionTrace;
import com.newrelic.agent.sql.SqlTrace;
import com.newrelic.agent.stats.StatsEngine;
import java.util.Map;
import com.newrelic.agent.profile.IProfile;
import java.util.List;
import com.newrelic.agent.service.Service;

public interface IRPMService extends Service
{
    List<Long> sendProfileData(List<IProfile> p0) throws Exception;
    
    boolean isConnected();
    
    Map<String, Object> launch() throws Exception;
    
    String getHostString();
    
    void harvest(StatsEngine p0) throws Exception;
    
    List<List<?>> getAgentCommands() throws Exception;
    
    void sendCommandResults(Map<Long, Object> p0) throws Exception;
    
    void sendSqlTraceData(List<SqlTrace> p0) throws Exception;
    
    void sendTransactionTraceData(List<TransactionTrace> p0) throws Exception;
    
    String getApplicationName();
    
    void reconnect();
    
    ErrorService getErrorService();
    
    boolean isMainApp();
    
    boolean hasEverConnected();
    
    String getTransactionNamingScheme();
    
    long getConnectionTimestamp();
    
    void sendModules(List<Jar> p0) throws Exception;
    
    void sendAnalyticsEvents(Collection<TransactionEvent> p0) throws Exception;
    
    void sendCustomAnalyticsEvents(Collection<CustomInsightsEvent> p0) throws Exception;
    
    Collection<?> getXRaySessionInfo(Collection<Long> p0) throws Exception;
}
