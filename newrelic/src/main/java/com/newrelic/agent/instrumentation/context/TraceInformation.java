// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.context;

import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Set;
import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Map;

public class TraceInformation
{
    private Map<Method, TraceDetails> traces;
    private Set<Method> ignoreApdexMethods;
    private Set<Method> ignoreTransactionMethods;
    
    public Map<Method, TraceDetails> getTraceAnnotations() {
        return (this.traces == null) ? Collections.emptyMap() : Collections.unmodifiableMap((Map<? extends Method, ? extends TraceDetails>)this.traces);
    }
    
    void pullAll(final Map<Method, TraceDetails> tracedMethods) {
        if (this.traces == null) {
            this.traces = (Map<Method, TraceDetails>)Maps.newHashMap((Map<?, ?>)tracedMethods);
        }
        else {
            for (final Map.Entry<Method, TraceDetails> entry : tracedMethods.entrySet()) {
                this.putTraceAnnotation(entry.getKey(), entry.getValue());
            }
        }
    }
    
    void putTraceAnnotation(final Method method, TraceDetails trace) {
        if (this.traces == null) {
            this.traces = (Map<Method, TraceDetails>)Maps.newHashMap();
        }
        else {
            final TraceDetails existing = this.traces.get(method);
            if (existing != null) {
                Agent.LOG.log(Level.FINEST, "Merging trace details {0} and {1} for method {2}", new Object[] { existing, trace, method });
                trace = TraceDetailsBuilder.merge(existing, trace);
            }
        }
        this.traces.put(method, trace);
    }
    
    public Set<Method> getIgnoreApdexMethods() {
        return (this.ignoreApdexMethods == null) ? Collections.emptySet() : this.ignoreApdexMethods;
    }
    
    public Set<Method> getIgnoreTransactionMethods() {
        return (this.ignoreTransactionMethods == null) ? Collections.emptySet() : this.ignoreTransactionMethods;
    }
    
    public void addIgnoreApdexMethod(final String methodName, final String methodDesc) {
        if (this.ignoreApdexMethods == null) {
            this.ignoreApdexMethods = (Set<Method>)Sets.newHashSet();
        }
        this.ignoreApdexMethods.add(new Method(methodName, methodDesc));
    }
    
    public void addIgnoreTransactionMethod(final String methodName, final String methodDesc) {
        if (this.ignoreTransactionMethods == null) {
            this.ignoreTransactionMethods = (Set<Method>)Sets.newHashSet();
        }
        this.ignoreTransactionMethods.add(new Method(methodName, methodDesc));
    }
    
    public void addIgnoreTransactionMethod(final Method m) {
        if (this.ignoreTransactionMethods == null) {
            this.ignoreTransactionMethods = (Set<Method>)Sets.newHashSet();
        }
        this.ignoreTransactionMethods.add(m);
    }
    
    public boolean isMatch() {
        return !this.getTraceAnnotations().isEmpty() || !this.getIgnoreApdexMethods().isEmpty() || !this.getIgnoreTransactionMethods().isEmpty();
    }
}
