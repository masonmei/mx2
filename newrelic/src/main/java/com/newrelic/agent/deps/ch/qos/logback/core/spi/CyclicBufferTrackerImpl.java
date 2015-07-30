// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.spi;

import java.util.LinkedList;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.helpers.CyclicBuffer;
import java.util.HashMap;
import java.util.Map;

public class CyclicBufferTrackerImpl<E> implements CyclicBufferTracker<E>
{
    int bufferSize;
    int maxNumBuffers;
    int bufferCount;
    static final int DELAY_BETWEEN_CLEARING_STALE_BUFFERS = 300000;
    boolean isStarted;
    private Map<String, Entry> map;
    private Entry head;
    private Entry tail;
    long lastCheck;
    
    public CyclicBufferTrackerImpl() {
        this.bufferSize = 256;
        this.maxNumBuffers = 64;
        this.bufferCount = 0;
        this.isStarted = false;
        this.map = new HashMap<String, Entry>();
        this.lastCheck = 0L;
        this.head = new Entry(null, null, 0L);
        this.tail = this.head;
    }
    
    public int getBufferSize() {
        return this.bufferSize;
    }
    
    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    public int getMaxNumberOfBuffers() {
        return this.maxNumBuffers;
    }
    
    public void setMaxNumberOfBuffers(final int maxNumBuffers) {
        this.maxNumBuffers = maxNumBuffers;
    }
    
    public CyclicBuffer<E> getOrCreate(final String key, final long timestamp) {
        final Entry existing = this.map.get(key);
        if (existing == null) {
            return this.processNewEntry(key, timestamp);
        }
        existing.setTimestamp(timestamp);
        this.moveToTail(existing);
        return existing.value;
    }
    
    public void removeBuffer(final String key) {
        final Entry existing = this.map.get(key);
        if (existing != null) {
            --this.bufferCount;
            this.map.remove(key);
            this.unlink(existing);
            final CyclicBuffer<E> cb = existing.value;
            if (cb != null) {
                cb.clear();
            }
        }
    }
    
    private CyclicBuffer<E> processNewEntry(final String key, final long timestamp) {
        final CyclicBuffer<E> cb = new CyclicBuffer<E>(this.bufferSize);
        final Entry entry = new Entry(key, cb, timestamp);
        this.map.put(key, entry);
        ++this.bufferCount;
        this.linkBeforeTail(entry);
        if (this.bufferCount >= this.maxNumBuffers) {
            this.removeHead();
        }
        return cb;
    }
    
    private void removeHead() {
        final CyclicBuffer cb = this.head.value;
        if (cb != null) {
            cb.clear();
        }
        this.map.remove(this.head.key);
        --this.bufferCount;
        this.head = this.head.next;
        this.head.prev = null;
    }
    
    private void moveToTail(final Entry e) {
        this.unlink(e);
        this.linkBeforeTail(e);
    }
    
    private void unlink(final Entry e) {
        if (e.prev != null) {
            e.prev.next = e.next;
        }
        if (e.next != null) {
            e.next.prev = e.prev;
        }
        if (this.head == e) {
            this.head = e.next;
        }
    }
    
    public synchronized void clearStaleBuffers(final long now) {
        if (this.lastCheck + 300000L > now) {
            return;
        }
        this.lastCheck = now;
        while (this.head.value != null && this.isEntryStale(this.head, now)) {
            this.removeHead();
        }
    }
    
    public int size() {
        return this.map.size();
    }
    
    private boolean isEntryStale(final Entry entry, final long now) {
        return entry.timestamp + 1800000L < now;
    }
    
    List<String> keyList() {
        final List<String> result = new LinkedList<String>();
        for (Entry e = this.head; e != this.tail; e = e.next) {
            result.add(e.key);
        }
        return result;
    }
    
    private void linkBeforeTail(final Entry e) {
        if (this.head == this.tail) {
            this.head = e;
        }
        final Entry preTail = this.tail.prev;
        if (preTail != null) {
            preTail.next = e;
        }
        e.prev = preTail;
        e.next = this.tail;
        this.tail.prev = e;
    }
    
    private class Entry
    {
        Entry next;
        Entry prev;
        String key;
        CyclicBuffer<E> value;
        long timestamp;
        
        Entry(final String k, final CyclicBuffer<E> v, final long timestamp) {
            this.key = k;
            this.value = v;
            this.timestamp = timestamp;
        }
        
        public void setTimestamp(final long timestamp) {
            this.timestamp = timestamp;
        }
        
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.key == null) ? 0 : this.key.hashCode());
            return result;
        }
        
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final Entry other = (Entry)obj;
            if (this.key == null) {
                if (other.key != null) {
                    return false;
                }
            }
            else if (!this.key.equals(other.key)) {
                return false;
            }
            if (this.value == null) {
                if (other.value != null) {
                    return false;
                }
            }
            else if (!this.value.equals(other.value)) {
                return false;
            }
            return true;
        }
        
        public String toString() {
            return "(" + this.key + ", " + this.value + ")";
        }
    }
}
