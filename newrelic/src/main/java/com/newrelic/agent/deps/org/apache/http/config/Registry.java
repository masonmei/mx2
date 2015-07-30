// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.config;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;

@ThreadSafe
public final class Registry<I> implements Lookup<I>
{
    private final Map<String, I> map;
    
    Registry(final Map<String, I> map) {
        this.map = new ConcurrentHashMap<String, I>((Map<? extends String, ? extends I>)map);
    }
    
    public I lookup(final String key) {
        if (key == null) {
            return null;
        }
        return this.map.get(key.toLowerCase(Locale.US));
    }
    
    public String toString() {
        return this.map.toString();
    }
}
