// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.api.agent;

import java.util.Map;
import com.newrelic.agent.bridge.AgentBridge;

public final class NewRelic
{
    public static Agent getAgent() {
        return (Agent)AgentBridge.agent;
    }
    
    public static void recordMetric(final String name, final float value) {
        getAgent().getMetricAggregator().recordMetric(name, value);
    }
    
    public static void recordResponseTimeMetric(final String name, final long millis) {
        getAgent().getMetricAggregator().recordResponseTimeMetric(name, millis);
    }
    
    public static void incrementCounter(final String name) {
        getAgent().getMetricAggregator().incrementCounter(name);
    }
    
    public static void incrementCounter(final String name, final int count) {
        getAgent().getMetricAggregator().incrementCounter(name, count);
    }
    
    public static void noticeError(final Throwable throwable, final Map<String, String> params) {
        AgentBridge.publicApi.noticeError(throwable, params);
    }
    
    public static void noticeError(final Throwable throwable) {
        AgentBridge.publicApi.noticeError(throwable);
    }
    
    public static void noticeError(final String message, final Map<String, String> params) {
        AgentBridge.publicApi.noticeError(message, params);
    }
    
    public static void noticeError(final String message) {
        AgentBridge.publicApi.noticeError(message);
    }
    
    public static void addCustomParameter(final String key, final Number value) {
        AgentBridge.publicApi.addCustomParameter(key, value);
    }
    
    public static void addCustomParameter(final String key, final String value) {
        AgentBridge.publicApi.addCustomParameter(key, value);
    }
    
    public static void setTransactionName(final String category, final String name) {
        AgentBridge.publicApi.setTransactionName(category, name);
    }
    
    public static void ignoreTransaction() {
        AgentBridge.publicApi.ignoreTransaction();
    }
    
    public static void ignoreApdex() {
        AgentBridge.publicApi.ignoreApdex();
    }
    
    public static void setRequestAndResponse(final Request request, final Response response) {
        AgentBridge.publicApi.setRequestAndResponse(request, response);
    }
    
    public static String getBrowserTimingHeader() {
        return AgentBridge.publicApi.getBrowserTimingHeader();
    }
    
    public static String getBrowserTimingFooter() {
        return AgentBridge.publicApi.getBrowserTimingFooter();
    }
    
    public static void setUserName(final String name) {
        AgentBridge.publicApi.setUserName(name);
    }
    
    public static void setAccountName(final String name) {
        AgentBridge.publicApi.setAccountName(name);
    }
    
    public static void setProductName(final String name) {
        AgentBridge.publicApi.setProductName(name);
    }
}
