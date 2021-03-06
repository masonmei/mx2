// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.params;

import java.util.Set;

@Deprecated
public abstract class AbstractHttpParams implements HttpParams, HttpParamsNames
{
    public long getLongParameter(final String name, final long defaultValue) {
        final Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (Long)param;
    }
    
    public HttpParams setLongParameter(final String name, final long value) {
        this.setParameter(name, value);
        return this;
    }
    
    public int getIntParameter(final String name, final int defaultValue) {
        final Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (Integer)param;
    }
    
    public HttpParams setIntParameter(final String name, final int value) {
        this.setParameter(name, value);
        return this;
    }
    
    public double getDoubleParameter(final String name, final double defaultValue) {
        final Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (Double)param;
    }
    
    public HttpParams setDoubleParameter(final String name, final double value) {
        this.setParameter(name, value);
        return this;
    }
    
    public boolean getBooleanParameter(final String name, final boolean defaultValue) {
        final Object param = this.getParameter(name);
        if (param == null) {
            return defaultValue;
        }
        return (Boolean)param;
    }
    
    public HttpParams setBooleanParameter(final String name, final boolean value) {
        this.setParameter(name, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }
    
    public boolean isParameterTrue(final String name) {
        return this.getBooleanParameter(name, false);
    }
    
    public boolean isParameterFalse(final String name) {
        return !this.getBooleanParameter(name, false);
    }
    
    public Set<String> getNames() {
        throw new UnsupportedOperationException();
    }
}
