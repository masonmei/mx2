// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn.tsccm;

import com.newrelic.agent.deps.org.apache.http.conn.ClientConnectionManager;
import com.newrelic.agent.deps.org.apache.http.impl.conn.AbstractPoolEntry;
import com.newrelic.agent.deps.org.apache.http.impl.conn.AbstractPooledConnAdapter;

@Deprecated
public class BasicPooledConnAdapter extends AbstractPooledConnAdapter
{
    protected BasicPooledConnAdapter(final ThreadSafeClientConnManager tsccm, final AbstractPoolEntry entry) {
        super(tsccm, entry);
        this.markReusable();
    }
    
    protected ClientConnectionManager getManager() {
        return super.getManager();
    }
    
    protected AbstractPoolEntry getPoolEntry() {
        return super.getPoolEntry();
    }
    
    protected void detach() {
        super.detach();
    }
}
