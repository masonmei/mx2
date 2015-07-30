// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.errors;

import java.util.Iterator;
import com.newrelic.agent.util.StackTraces;
import java.util.HashMap;
import java.util.Collection;
import java.lang.management.ThreadInfo;
import java.util.Map;

public class DeadlockTraceError extends TracedError
{
    private final String message;
    private final String exceptionClass;
    private final Map<String, StackTraceElement[]> stackTraces;
    
    private DeadlockTraceError(final String appName, final String frontendMetricName, final String message, final String exceptionClass, final Map<String, StackTraceElement[]> stackTraces, final String requestPath, final Map<String, String> params) {
        super(appName, frontendMetricName, requestPath, System.currentTimeMillis(), null, null, null, params, null);
        this.stackTraces = stackTraces;
        this.message = message;
        this.exceptionClass = exceptionClass;
    }
    
    public DeadlockTraceError(final String appName, final ThreadInfo thread, final Map<String, StackTraceElement[]> stackTraces, final Map<String, String> params) {
        this(appName, "Deadlock", "Deadlocked thread: " + thread.getThreadName(), "Deadlock", stackTraces, "", params);
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getExceptionClass() {
        return this.exceptionClass;
    }
    
    public Collection<String> stackTrace() {
        return null;
    }
    
    public boolean incrementsErrorMetric() {
        return false;
    }
    
    public Map<String, Collection<String>> stackTraces() {
        final Map<String, Collection<String>> traces = new HashMap<String, Collection<String>>();
        for (final Map.Entry<String, StackTraceElement[]> entry : this.stackTraces.entrySet()) {
            traces.put(entry.getKey(), StackTraces.stackTracesToStrings(entry.getValue()));
        }
        return traces;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.stackTraces == null) ? 0 : this.stackTraces.hashCode());
        return result;
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
        final DeadlockTraceError other = (DeadlockTraceError)obj;
        if (this.stackTraces == null) {
            if (other.stackTraces != null) {
                return false;
            }
        }
        else if (!this.stackTraces.equals(other.stackTraces)) {
            return false;
        }
        return true;
    }
}
