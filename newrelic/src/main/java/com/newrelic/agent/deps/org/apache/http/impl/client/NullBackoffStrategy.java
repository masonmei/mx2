// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.client.ConnectionBackoffStrategy;

public class NullBackoffStrategy implements ConnectionBackoffStrategy
{
    public boolean shouldBackoff(final Throwable t) {
        return false;
    }
    
    public boolean shouldBackoff(final HttpResponse resp) {
        return false;
    }
}
