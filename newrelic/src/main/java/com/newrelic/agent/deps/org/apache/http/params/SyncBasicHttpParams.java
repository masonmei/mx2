// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.params;

import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;

@Deprecated
@ThreadSafe
public class SyncBasicHttpParams extends BasicHttpParams
{
    private static final long serialVersionUID = 5387834869062660642L;
    
    public synchronized boolean removeParameter(final String name) {
        return super.removeParameter(name);
    }
    
    public synchronized HttpParams setParameter(final String name, final Object value) {
        return super.setParameter(name, value);
    }
    
    public synchronized Object getParameter(final String name) {
        return super.getParameter(name);
    }
    
    public synchronized boolean isParameterSet(final String name) {
        return super.isParameterSet(name);
    }
    
    public synchronized boolean isParameterSetLocally(final String name) {
        return super.isParameterSetLocally(name);
    }
    
    public synchronized void setParameters(final String[] names, final Object value) {
        super.setParameters(names, value);
    }
    
    public synchronized void clear() {
        super.clear();
    }
    
    public synchronized Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
