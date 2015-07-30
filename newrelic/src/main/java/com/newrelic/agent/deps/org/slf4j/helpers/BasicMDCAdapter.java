// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j.helpers;

import java.util.Set;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.agent.deps.org.slf4j.spi.MDCAdapter;

public class BasicMDCAdapter implements MDCAdapter
{
    private InheritableThreadLocal inheritableThreadLocal;
    static boolean IS_JDK14;
    
    public BasicMDCAdapter() {
        this.inheritableThreadLocal = new InheritableThreadLocal();
    }
    
    static boolean isJDK14() {
        try {
            final String javaVersion = System.getProperty("java.version");
            return javaVersion.startsWith("1.4");
        }
        catch (SecurityException se) {
            return false;
        }
    }
    
    public void put(final String key, final String val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map map = (Map)this.inheritableThreadLocal.get();
        if (map == null) {
            map = Collections.synchronizedMap(new HashMap<Object, Object>());
            this.inheritableThreadLocal.set(map);
        }
        map.put(key, val);
    }
    
    public String get(final String key) {
        final Map Map = (Map)this.inheritableThreadLocal.get();
        if (Map != null && key != null) {
            return Map.get(key);
        }
        return null;
    }
    
    public void remove(final String key) {
        final Map map = (Map)this.inheritableThreadLocal.get();
        if (map != null) {
            map.remove(key);
        }
    }
    
    public void clear() {
        final Map map = (Map)this.inheritableThreadLocal.get();
        if (map != null) {
            map.clear();
            if (isJDK14()) {
                this.inheritableThreadLocal.set(null);
            }
            else {
                this.inheritableThreadLocal.remove();
            }
        }
    }
    
    public Set getKeys() {
        final Map map = (Map)this.inheritableThreadLocal.get();
        if (map != null) {
            return map.keySet();
        }
        return null;
    }
    
    public Map getCopyOfContextMap() {
        final Map oldMap = (Map)this.inheritableThreadLocal.get();
        if (oldMap != null) {
            final Map newMap = Collections.synchronizedMap(new HashMap<Object, Object>());
            synchronized (oldMap) {
                newMap.putAll(oldMap);
            }
            return newMap;
        }
        return null;
    }
    
    public void setContextMap(final Map contextMap) {
        final Map map = Collections.synchronizedMap(new HashMap<Object, Object>(contextMap));
        this.inheritableThreadLocal.set(map);
    }
    
    static {
        BasicMDCAdapter.IS_JDK14 = isJDK14();
    }
}
