// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

class SystemClock implements Clock
{
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
