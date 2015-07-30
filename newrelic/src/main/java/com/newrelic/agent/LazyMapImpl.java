// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.util.Collection;
import java.util.Set;
import com.newrelic.agent.deps.com.google.common.collect.MapMaker;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Map;

public class LazyMapImpl<K, V> implements Map<K, V>
{
    private final AtomicReference<Map<K, V>> parameters;
    private final MapMaker factory;
    
    public LazyMapImpl() {
        this(5);
    }
    
    public LazyMapImpl(final int initialSize) {
        this(new MapMaker().initialCapacity(initialSize).concurrencyLevel(1));
    }
    
    public LazyMapImpl(final MapMaker factory) {
        this.parameters = new AtomicReference<Map<K, V>>();
        this.factory = factory;
    }
    
    private Map<K, V> getParameters() {
        if (this.parameters.get() == null) {
            this.parameters.compareAndSet(null, (Map<K, V>)this.factory.makeMap());
        }
        return this.parameters.get();
    }
    
    public V put(final K key, final V value) {
        return this.getParameters().put(key, value);
    }
    
    public void putAll(final Map<? extends K, ? extends V> params) {
        if (params != null && !params.isEmpty()) {
            this.getParameters().putAll(params);
        }
    }
    
    public V remove(final Object key) {
        if (this.parameters.get() == null) {
            return null;
        }
        return this.getParameters().remove(key);
    }
    
    public V get(final Object key) {
        if (this.parameters.get() == null) {
            return null;
        }
        return this.getParameters().get(key);
    }
    
    public void clear() {
        if (this.parameters.get() != null) {
            this.parameters.get().clear();
        }
    }
    
    public int size() {
        if (this.parameters.get() == null) {
            return 0;
        }
        return this.getParameters().size();
    }
    
    public boolean isEmpty() {
        return this.parameters.get() == null || this.getParameters().isEmpty();
    }
    
    public boolean containsKey(final Object key) {
        return this.parameters.get() != null && this.getParameters().containsKey(key);
    }
    
    public boolean containsValue(final Object value) {
        return this.parameters.get() != null && this.getParameters().containsValue(value);
    }
    
    public Set<K> keySet() {
        return this.getParameters().keySet();
    }
    
    public Collection<V> values() {
        return this.getParameters().values();
    }
    
    public Set<Entry<K, V>> entrySet() {
        return this.getParameters().entrySet();
    }
}
