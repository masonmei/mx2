// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.turbo;

import java.util.Map;
import java.util.LinkedHashMap;

class LRUMessageCache extends LinkedHashMap<String, Integer>
{
    private static final long serialVersionUID = 1L;
    final int cacheSize;
    
    LRUMessageCache(final int cacheSize) {
        super((int)(cacheSize * 1.3333334f), 0.75f, true);
        if (cacheSize < 1) {
            throw new IllegalArgumentException("Cache size cannot be smaller than 1");
        }
        this.cacheSize = cacheSize;
    }
    
    int getMessageCountAndThenIncrement(final String msg) {
        if (msg == null) {
            return 0;
        }
        Integer i;
        synchronized (this) {
            i = super.get(msg);
            if (i == null) {
                i = 0;
            }
            else {
                ++i;
            }
            super.put(msg, i);
        }
        return i;
    }
    
    protected boolean removeEldestEntry(final Map.Entry eldest) {
        return this.size() > this.cacheSize;
    }
    
    public synchronized void clear() {
        super.clear();
    }
}
