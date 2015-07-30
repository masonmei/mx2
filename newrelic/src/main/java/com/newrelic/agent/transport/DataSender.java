// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transport;

import com.newrelic.agent.service.module.Jar;
import com.newrelic.agent.trace.TransactionTrace;
import com.newrelic.agent.sql.SqlTrace;
import com.newrelic.agent.profile.IProfile;
import com.newrelic.agent.MetricData;
import com.newrelic.agent.service.analytics.CustomInsightsEvent;
import com.newrelic.agent.service.analytics.TransactionEvent;
import java.util.Collection;
import com.newrelic.agent.errors.TracedError;
import java.util.List;
import java.util.Map;

public interface DataSender
{
    Map<String, Object> connect(Map<String, Object> p0) throws Exception;
    
    List<List<?>> getAgentCommands() throws Exception;
    
    void queuePingCommand() throws Exception;
    
    void sendCommandResults(Map<Long, Object> p0) throws Exception;
    
    void sendErrorData(List<TracedError> p0) throws Exception;
    
    void sendAnalyticsEvents(Collection<TransactionEvent> p0) throws Exception;
    
    void sendCustomAnalyticsEvents(Collection<CustomInsightsEvent> p0) throws Exception;
    
    List<List<?>> sendMetricData(long p0, long p1, List<MetricData> p2) throws Exception;
    
    List<Long> sendProfileData(List<IProfile> p0) throws Exception;
    
    void sendSqlTraceData(List<SqlTrace> p0) throws Exception;
    
    void sendTransactionTraceData(List<TransactionTrace> p0) throws Exception;
    
    void sendModules(List<Jar> p0) throws Exception;
    
    void shutdown(long p0) throws Exception;
    
    List<?> getXRayParameters(Collection<Long> p0) throws Exception;
}
