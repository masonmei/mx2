// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.analytics;

import java.util.ListIterator;
import java.util.Arrays;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Collection;
import com.newrelic.agent.service.ServiceUtils;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

public class FixedSizeArrayList<E> implements List<E>
{
    private final Object[] data;
    private final AtomicInteger volatileMemoryBarrier;
    protected final int size;
    protected final AtomicInteger numberOfTries;
    
    public FixedSizeArrayList(final int size) {
        this.volatileMemoryBarrier = new AtomicInteger(0);
        this.numberOfTries = new AtomicInteger();
        this.data = new Object[size];
        this.size = size;
    }
    
    public E get(final int index) {
        this.rangeCheck(index);
        ServiceUtils.readMemoryBarrier(this.volatileMemoryBarrier);
        return (E)this.data[index];
    }
    
    public boolean add(final E t) {
        final Integer slot = this.getSlot();
        if (slot == null) {
            return false;
        }
        this.set(slot, t);
        return true;
    }
    
    public boolean addAll(final Collection<? extends E> c) {
        boolean modified = false;
        for (final E e : c) {
            modified |= this.add(e);
        }
        return modified;
    }
    
    public E set(final int slot, final E element) {
        this.rangeCheck(slot);
        ServiceUtils.readMemoryBarrier(this.volatileMemoryBarrier);
        final E oldValue = (E)this.data[slot];
        this.data[slot] = element;
        ServiceUtils.writeMemoryBarrier(this.volatileMemoryBarrier);
        return oldValue;
    }
    
    private void rangeCheck(final int index) {
        if (index >= this.data.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.data.length);
        }
    }
    
    public Integer getSlot() {
        final int insertIndex = this.numberOfTries.getAndIncrement();
        if (insertIndex >= this.data.length) {
            return null;
        }
        return insertIndex;
    }
    
    int getNumberOfTries() {
        return this.numberOfTries.get();
    }
    
    public int size() {
        return Math.min(this.data.length, this.numberOfTries.get());
    }
    
    public boolean isEmpty() {
        return this.numberOfTries.get() == 0;
    }
    
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int cursor;
            
            public boolean hasNext() {
                return this.cursor != FixedSizeArrayList.this.size();
            }
            
            public E next() {
                final int i = this.cursor;
                if (i >= FixedSizeArrayList.this.size()) {
                    throw new NoSuchElementException();
                }
                this.cursor = i + 1;
                ServiceUtils.readMemoryBarrier(FixedSizeArrayList.this.volatileMemoryBarrier);
                return (E)FixedSizeArrayList.this.data[i];
            }
            
            public void remove() {
                Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
            }
        };
    }
    
    public Object[] toArray() {
        return Arrays.copyOf(this.data, this.size());
    }
    
    public boolean contains(final Object o) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return false;
    }
    
    public <T> T[] toArray(final T[] a) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return null;
    }
    
    public boolean remove(final Object o) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return false;
    }
    
    public boolean containsAll(final Collection<?> c) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return false;
    }
    
    public boolean addAll(final int index, final Collection<? extends E> c) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return false;
    }
    
    public boolean removeAll(final Collection<?> c) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return false;
    }
    
    public boolean retainAll(final Collection<?> c) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return false;
    }
    
    public void clear() {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
    }
    
    public void add(final int index, final E element) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
    }
    
    public E remove(final int index) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return null;
    }
    
    public int indexOf(final Object o) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return -1;
    }
    
    public int lastIndexOf(final Object o) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return -1;
    }
    
    public ListIterator<E> listIterator() {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return null;
    }
    
    public ListIterator<E> listIterator(final int index) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return null;
    }
    
    public List<E> subList(final int fromIndex, final int toIndex) {
        Agent.LOG.log(Level.FINE, (Throwable)new UnsupportedOperationException(), "Method not implemented.", new Object[0]);
        return null;
    }
}
