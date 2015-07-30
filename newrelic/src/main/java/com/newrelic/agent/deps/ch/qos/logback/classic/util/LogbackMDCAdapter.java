// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.util;

import java.util.Set;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.agent.deps.org.slf4j.spi.MDCAdapter;

public final class LogbackMDCAdapter implements MDCAdapter
{
    final InheritableThreadLocal<Map<String, String>> copyOnInheritThreadLocal;
    private static final int WRITE_OPERATION = 1;
    private static final int READ_OPERATION = 2;
    final ThreadLocal<Integer> lastOperation;
    
    public LogbackMDCAdapter() {
        this.copyOnInheritThreadLocal = new InheritableThreadLocal<Map<String, String>>();
        this.lastOperation = new ThreadLocal<Integer>();
    }
    
    private Integer getAndSetLastOperation(final int op) {
        final Integer lastOp = this.lastOperation.get();
        this.lastOperation.set(op);
        return lastOp;
    }
    
    private boolean wasLastOpReadOrNull(final Integer lastOp) {
        return lastOp == null || lastOp == 2;
    }
    
    private Map<String, String> duplicateAndInsertNewMap(final Map<String, String> oldMap) {
        final Map<String, String> newMap = Collections.synchronizedMap(new HashMap<String, String>());
        if (oldMap != null) {
            synchronized (oldMap) {
                newMap.putAll(oldMap);
            }
        }
        this.copyOnInheritThreadLocal.set(newMap);
        return newMap;
    }
    
    public void put(final String key, final String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        final Map<String, String> oldMap = this.copyOnInheritThreadLocal.get();
        final Integer lastOp = this.getAndSetLastOperation(1);
        if (this.wasLastOpReadOrNull(lastOp) || oldMap == null) {
            final Map<String, String> newMap = this.duplicateAndInsertNewMap(oldMap);
            newMap.put(key, val);
        }
        else {
            oldMap.put(key, val);
        }
    }
    
    public void remove(final String key) {
        if (key == null) {
            return;
        }
        final Map<String, String> oldMap = this.copyOnInheritThreadLocal.get();
        if (oldMap == null) {
            return;
        }
        final Integer lastOp = this.getAndSetLastOperation(1);
        if (this.wasLastOpReadOrNull(lastOp)) {
            final Map<String, String> newMap = this.duplicateAndInsertNewMap(oldMap);
            newMap.remove(key);
        }
        else {
            oldMap.remove(key);
        }
    }
    
    public void clear() {
        this.lastOperation.set(1);
        this.copyOnInheritThreadLocal.remove();
    }
    
    public String get(final String key) {
        final Map<String, String> map = this.getPropertyMap();
        if (map != null && key != null) {
            return map.get(key);
        }
        return null;
    }
    
    public Map<String, String> getPropertyMap() {
        this.lastOperation.set(2);
        return this.copyOnInheritThreadLocal.get();
    }
    
    public Set<String> getKeys() {
        final Map<String, String> map = this.getPropertyMap();
        if (map != null) {
            return map.keySet();
        }
        return null;
    }
    
    public Map getCopyOfContextMap() {
        this.lastOperation.set(2);
        final Map<String, String> hashMap = this.copyOnInheritThreadLocal.get();
        if (hashMap == null) {
            return null;
        }
        return new HashMap(hashMap);
    }
    
    public void setContextMap(final Map contextMap) {
        this.lastOperation.set(1);
        final Map<String, String> newMap = Collections.synchronizedMap(new HashMap<String, String>());
        newMap.putAll(contextMap);
        this.copyOnInheritThreadLocal.set(newMap);
    }
}
