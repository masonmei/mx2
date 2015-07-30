// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.errors;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

public class HttpTracedError extends TracedError
{
    private final int responseStatus;
    private final String errorMessage;
    
    public HttpTracedError(final String appName, final String frontendMetricName, final int responseStatus, final String errorMessage, final String requestPath, final long timestamp, final Map<String, Map<String, String>> prefixedParams, final Map<String, Object> userParams, final Map<String, Object> agentParams, final Map<String, String> errorParams, final Map<String, Object> intrinsics) {
        super(appName, frontendMetricName, requestPath, timestamp, prefixedParams, userParams, agentParams, errorParams, intrinsics);
        this.responseStatus = responseStatus;
        if (errorMessage == null) {
            if (responseStatus >= 400 && responseStatus < 500) {
                this.errorMessage = "HttpClientError " + responseStatus;
            }
            else {
                this.errorMessage = "HttpServerError " + responseStatus;
            }
        }
        else {
            this.errorMessage = errorMessage;
        }
    }
    
    public Collection<String> stackTrace() {
        return null;
    }
    
    public String getExceptionClass() {
        return this.getMessage();
    }
    
    public String getMessage() {
        return this.errorMessage;
    }
    
    public String toString() {
        return MessageFormat.format("{0} ({1})", this.getMessage(), this.responseStatus);
    }
}
