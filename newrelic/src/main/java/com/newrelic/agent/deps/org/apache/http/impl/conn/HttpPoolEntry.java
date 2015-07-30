// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.org.apache.http.conn.routing.RouteTracker;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import com.newrelic.agent.deps.org.apache.http.conn.OperatedClientConnection;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.pool.PoolEntry;

@Deprecated
class HttpPoolEntry extends PoolEntry<HttpRoute, OperatedClientConnection>
{
    private final Log log;
    private final RouteTracker tracker;
    
    public HttpPoolEntry(final Log log, final String id, final HttpRoute route, final OperatedClientConnection conn, final long timeToLive, final TimeUnit tunit) {
        super(id, route, conn, timeToLive, tunit);
        this.log = log;
        this.tracker = new RouteTracker(route);
    }
    
    public boolean isExpired(final long now) {
        final boolean expired = super.isExpired(now);
        if (expired && this.log.isDebugEnabled()) {
            this.log.debug("Connection " + this + " expired @ " + new Date(this.getExpiry()));
        }
        return expired;
    }
    
    RouteTracker getTracker() {
        return this.tracker;
    }
    
    HttpRoute getPlannedRoute() {
        return this.getRoute();
    }
    
    HttpRoute getEffectiveRoute() {
        return this.tracker.toRoute();
    }
    
    public boolean isClosed() {
        final OperatedClientConnection conn = this.getConnection();
        return !conn.isOpen();
    }
    
    public void close() {
        final OperatedClientConnection conn = this.getConnection();
        try {
            conn.close();
        }
        catch (IOException ex) {
            this.log.debug("I/O error closing connection", ex);
        }
    }
}
