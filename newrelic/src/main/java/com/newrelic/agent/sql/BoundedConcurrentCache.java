// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class BoundedConcurrentCache<K, V extends Comparable<V>>
{
    private final int maxCapacity;
    private final PriorityQueue<V> priorityQueue;
    private final Map<K, V> cache;
    private final Map<V, K> inverseCache;
    
    public BoundedConcurrentCache(final int size) {
        this.maxCapacity = size;
        this.priorityQueue = new PriorityQueue<V>(size);
        this.cache = new HashMap<K, V>();
        this.inverseCache = new HashMap<V, K>();
    }
    
    public BoundedConcurrentCache(final int size, final Comparator<V> comparator) {
        this.maxCapacity = size;
        this.priorityQueue = new PriorityQueue<V>(size, comparator);
        this.cache = new HashMap<K, V>();
        this.inverseCache = new HashMap<V, K>();
    }
    
    public synchronized V get(final K sql) {
        return this.cache.get(sql);
    }
    
    public synchronized V putIfAbsent(final K key, final V value) {
        if (this.cache.containsKey(key)) {
            return this.cache.get(key);
        }
        if (this.priorityQueue.size() == this.maxCapacity) {
            final V val = this.priorityQueue.poll();
            final K sqlToRemove = this.inverseCache.get(val);
            this.cache.remove(sqlToRemove);
            this.inverseCache.remove(val);
        }
        this.priorityQueue.add(value);
        this.cache.put(key, value);
        this.inverseCache.put(value, key);
        return null;
    }
    
    public synchronized void putReplace(final K key, final V value) {
        if (this.cache.containsKey(key)) {
            final V valueToUpdate = this.cache.remove(key);
            this.inverseCache.remove(valueToUpdate);
            this.priorityQueue.remove(valueToUpdate);
        }
        this.putIfAbsent(key, value);
    }
    
    public synchronized int size() {
        return this.priorityQueue.size();
    }
    
    public synchronized void clear() {
        this.cache.clear();
        this.inverseCache.clear();
        this.priorityQueue.clear();
    }
    
    public synchronized List<V> asList() {
        final ArrayList<V> list = new ArrayList<V>();
        final Iterator<V> iter = this.priorityQueue.iterator();
        while (iter.hasNext()) {
            list.add(iter.next());
        }
        return list;
    }
}
