// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.errors;

import java.util.List;
import com.newrelic.agent.util.StackTraces;
import com.newrelic.agent.instrumentation.pointcuts.container.jetty.MultiException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ThrowableError extends TracedError
{
    private final Throwable throwable;
    
    public ThrowableError(final String appName, final String frontendMetricName, final Throwable error, final String requestPath, final long timestamp, final Map<String, Map<String, String>> prefixedParams, final Map<String, Object> userParams, final Map<String, Object> agentParams, final Map<String, String> errorParams, final Map<String, Object> intrinsics) {
        super(appName, frontendMetricName, requestPath, timestamp, prefixedParams, userParams, agentParams, errorParams, intrinsics);
        this.throwable = error;
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
    
    public String getMessage() {
        final String message = (this.throwable == null) ? null : ErrorService.getStrippedExceptionMessage(this.throwable);
        if (message == null) {
            return "";
        }
        return message;
    }
    
    public String getExceptionClass() {
        return this.throwable.getClass().getName();
    }
    
    public Collection<String> stackTrace() {
        final Collection<String> stackTrace = new ArrayList<String>();
        if (this.throwable instanceof MultiException) {
            final List<Throwable> throwables = ((MultiException)this.throwable).getThrowables();
            for (int i = 0; i < throwables.size(); ++i) {
                if (i > 0) {
                    stackTrace.add(" ");
                }
                stackTrace.addAll(StackTraces.stackTracesToStrings(throwables.get(i).getStackTrace()));
            }
        }
        else {
            Throwable t = this.throwable;
            boolean inner = false;
            while (t != null) {
                if (inner) {
                    stackTrace.add(" ");
                    stackTrace.add(" caused by " + t.toString());
                }
                stackTrace.addAll(StackTraces.stackTracesToStrings(t.getStackTrace()));
                t = (t.equals(t.getCause()) ? null : t.getCause());
                inner = true;
            }
        }
        return stackTrace;
    }
    
    public String toString() {
        return this.getMessage();
    }
    
    public int hashCode() {
        return this.throwable.hashCode();
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ThrowableError other = (ThrowableError)obj;
        return this.throwable.equals(other.throwable);
    }
}
