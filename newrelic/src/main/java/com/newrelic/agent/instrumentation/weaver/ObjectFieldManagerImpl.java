// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.com.google.common.collect.MapMaker;
import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.concurrent.ConcurrentMap;
import com.newrelic.agent.bridge.ObjectFieldManager;

class ObjectFieldManagerImpl implements ObjectFieldManager
{
    final ConcurrentMap<String, ConcurrentMap<Object, Object>> classObjectFields;
    
    public ObjectFieldManagerImpl() {
        this.classObjectFields = Maps.newConcurrentMap();
    }
    
    public Object getFieldContainer(final String className, final Object target) {
        final Map<Object, Object> map = this.classObjectFields.get(className);
        if (map != null) {
            return map.get(target);
        }
        return null;
    }
    
    public void initializeFields(final String className, final Object target, final Object fieldContainer) {
        final ConcurrentMap<Object, Object> map = this.classObjectFields.get(className);
        if (map != null) {
            final Object existing = map.putIfAbsent(target, fieldContainer);
            if (existing != null) {}
        }
    }
    
    public void createClassObjectFields(final String className) {
        final ConcurrentMap<Object, Object> existing = this.classObjectFields.putIfAbsent(className, new MapMaker().weakKeys().concurrencyLevel(8).makeMap());
        if (existing != null) {
            Agent.LOG.log(Level.FINEST, className, new Object[] { " already has an object field map" });
        }
    }
}
