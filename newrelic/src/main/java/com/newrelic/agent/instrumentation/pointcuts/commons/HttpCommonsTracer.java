// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.commons;

import java.lang.reflect.Method;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.IOTracer;
import com.newrelic.agent.tracers.AbstractCrossProcessTracer;

public class HttpCommonsTracer extends AbstractCrossProcessTracer implements IOTracer
{
    public HttpCommonsTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final String library, final String uri, final String methodName) {
        super(transaction, sig, object, host, library, uri, methodName);
    }
    
    protected String getHeaderValue(final Object returnValue, final String name) {
        final Object invocationTarget = this.getInvocationTarget();
        if (invocationTarget instanceof HttpMethodExtension) {
            final HttpMethodExtension httpMethod = (HttpMethodExtension)invocationTarget;
            final Object header = httpMethod._nr_getResponseHeader(name);
            return (header == null) ? null : this.getHeaderValue(header);
        }
        try {
            final Method method = invocationTarget.getClass().getMethod("getResponseHeader", String.class);
            final Object header = method.invoke(invocationTarget, name);
            if (header != null) {
                return (header instanceof NameValuePair) ? ((NameValuePair)header).getValue() : header.toString();
            }
        }
        catch (Throwable t) {
            Agent.LOG.log(Level.FINEST, "Error getting response header", t);
        }
        return null;
    }
    
    private String getHeaderValue(final Object header) {
        if (header == null) {
            return null;
        }
        if (header instanceof Header) {
            return ((Header)header).getValue();
        }
        return header.toString();
    }
}
