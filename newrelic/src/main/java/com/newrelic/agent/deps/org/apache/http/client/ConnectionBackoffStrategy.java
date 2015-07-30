// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.HttpResponse;

public interface ConnectionBackoffStrategy
{
    boolean shouldBackoff(Throwable p0);
    
    boolean shouldBackoff(HttpResponse p0);
}
