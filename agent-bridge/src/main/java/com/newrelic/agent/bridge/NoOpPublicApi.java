// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import java.util.Map;

class NoOpPublicApi implements PublicApi
{
    public void noticeError(final Throwable throwable, final Map<String, String> params) {
    }
    
    public void noticeError(final Throwable throwable) {
    }
    
    public void noticeError(final String message, final Map<String, String> params) {
    }
    
    public void noticeError(final String message) {
    }
    
    public void addCustomParameter(final String key, final Number value) {
    }
    
    public void addCustomParameter(final String key, final String value) {
    }
    
    public void setTransactionName(final String category, final String name) {
    }
    
    public void ignoreTransaction() {
    }
    
    public void ignoreApdex() {
    }
    
    public void setRequestAndResponse(final Request request, final Response response) {
    }
    
    public String getBrowserTimingHeader() {
        return "";
    }
    
    public String getBrowserTimingFooter() {
        return "";
    }
    
    public void setUserName(final String name) {
    }
    
    public void setAccountName(final String name) {
    }
    
    public void setProductName(final String name) {
    }
}
