// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn.tsccm;

import com.newrelic.agent.deps.org.apache.http.util.Args;
import java.lang.ref.ReferenceQueue;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import java.lang.ref.WeakReference;

@Deprecated
public class BasicPoolEntryRef extends WeakReference<BasicPoolEntry>
{
    private final HttpRoute route;
    
    public BasicPoolEntryRef(final BasicPoolEntry entry, final ReferenceQueue<Object> queue) {
        super(entry, queue);
        Args.notNull(entry, "Pool entry");
        this.route = entry.getPlannedRoute();
    }
    
    public final HttpRoute getRoute() {
        return this.route;
    }
}
