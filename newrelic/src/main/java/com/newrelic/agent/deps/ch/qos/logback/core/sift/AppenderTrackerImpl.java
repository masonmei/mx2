// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.sift;

import java.util.LinkedList;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import java.util.HashMap;
import java.util.Map;

public class AppenderTrackerImpl<E> implements AppenderTracker<E>
{
    Map<String, Entry> map;
    Entry head;
    Entry tail;
    long lastCheck;
    
    AppenderTrackerImpl() {
        this.map = new HashMap<String, Entry>();
        this.lastCheck = 0L;
        this.head = new Entry(null, null, 0L);
        this.tail = this.head;
    }
    
    public synchronized void put(final String key, final Appender<E> value, final long timestamp) {
        Entry entry = this.map.get(key);
        if (entry == null) {
            entry = new Entry(key, value, timestamp);
            this.map.put(key, entry);
        }
        this.moveToTail(entry);
    }
    
    public synchronized Appender<E> get(final String key, final long timestamp) {
        final Entry existing = this.map.get(key);
        if (existing == null) {
            return null;
        }
        existing.setTimestamp(timestamp);
        this.moveToTail(existing);
        return existing.value;
    }
    
    public synchronized void stopStaleAppenders(final long now) {
        if (this.lastCheck + 1000L > now) {
            return;
        }
        this.lastCheck = now;
        while (this.head.value != null && this.isEntryStale(this.head, now)) {
            final Appender<E> appender = this.head.value;
            appender.stop();
            this.removeHead();
        }
    }
    
    public synchronized void stopAndRemoveNow(final String key) {
        Entry e = this.head;
        Entry found = null;
        while (e != this.tail) {
            if (key.equals(e.key)) {
                found = e;
                break;
            }
            e = e.next;
        }
        if (found != null) {
            this.rearrangePreexistingLinks(e);
            this.map.remove(key);
            final Appender<E> appender = e.value;
            appender.stop();
        }
    }
    
    public List<String> keyList() {
        final List<String> result = new LinkedList<String>();
        for (Entry e = this.head; e != this.tail; e = e.next) {
            result.add(e.key);
        }
        return result;
    }
    
    private boolean isEntryStale(final Entry entry, final long now) {
        return !entry.value.isStarted() || entry.timestamp + 1800000L < now;
    }
    
    private void removeHead() {
        this.map.remove(this.head.key);
        this.head = this.head.next;
        this.head.prev = null;
    }
    
    private void moveToTail(final Entry e) {
        this.rearrangePreexistingLinks(e);
        this.rearrangeTailLinks(e);
    }
    
    private void rearrangePreexistingLinks(final Entry e) {
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
    
    private void rearrangeTailLinks(final Entry e) {
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
    
    public void dump() {
        Entry e = this.head;
        System.out.print("N:");
        while (e != null) {
            System.out.print(e.key + ", ");
            e = e.next;
        }
        System.out.println();
    }
    
    public List<Appender<E>> valueList() {
        final List<Appender<E>> result = new LinkedList<Appender<E>>();
        for (Entry e = this.head; e != this.tail; e = e.next) {
            result.add(e.value);
        }
        return result;
    }
    
    private class Entry
    {
        Entry next;
        Entry prev;
        String key;
        Appender<E> value;
        long timestamp;
        
        Entry(final String k, final Appender<E> v, final long timestamp) {
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
