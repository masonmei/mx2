// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import java.util.concurrent.ExecutionException;
import com.newrelic.agent.deps.org.apache.http.HttpClientConnection;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.org.apache.http.concurrent.Cancellable;

public interface ConnectionRequest extends Cancellable
{
    HttpClientConnection get(long p0, TimeUnit p1) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException;
}
