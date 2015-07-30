// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.protocol;

@Deprecated
public class SyncBasicHttpContext extends BasicHttpContext
{
    public SyncBasicHttpContext(final HttpContext parentContext) {
        super(parentContext);
    }
    
    public SyncBasicHttpContext() {
    }
    
    public synchronized Object getAttribute(final String id) {
        return super.getAttribute(id);
    }
    
    public synchronized void setAttribute(final String id, final Object obj) {
        super.setAttribute(id, obj);
    }
    
    public synchronized Object removeAttribute(final String id) {
        return super.removeAttribute(id);
    }
    
    public synchronized void clear() {
        super.clear();
    }
}
