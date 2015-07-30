// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.helpers;

import java.util.ArrayList;
import java.util.List;

public class CyclicBuffer<E>
{
    E[] ea;
    int first;
    int last;
    int numElems;
    int maxSize;
    
    public CyclicBuffer(final int maxSize) throws IllegalArgumentException {
        if (maxSize < 1) {
            throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
        }
        this.init(maxSize);
    }
    
    public CyclicBuffer(final CyclicBuffer<E> other) {
        this.maxSize = other.maxSize;
        this.ea = new Object[this.maxSize];
        System.arraycopy(other.ea, 0, this.ea, 0, this.maxSize);
        this.last = other.last;
        this.first = other.first;
        this.numElems = other.numElems;
    }
    
    private void init(final int maxSize) {
        this.maxSize = maxSize;
        this.ea = new Object[maxSize];
        this.first = 0;
        this.last = 0;
        this.numElems = 0;
    }
    
    public void clear() {
        this.init(this.maxSize);
    }
    
    public void add(final E event) {
        this.ea[this.last] = event;
        if (++this.last == this.maxSize) {
            this.last = 0;
        }
        if (this.numElems < this.maxSize) {
            ++this.numElems;
        }
        else if (++this.first == this.maxSize) {
            this.first = 0;
        }
    }
    
    public E get(final int i) {
        if (i < 0 || i >= this.numElems) {
            return null;
        }
        return (E)this.ea[(this.first + i) % this.maxSize];
    }
    
    public int getMaxSize() {
        return this.maxSize;
    }
    
    public E get() {
        E r = null;
        if (this.numElems > 0) {
            --this.numElems;
            r = (E)this.ea[this.first];
            this.ea[this.first] = null;
            if (++this.first == this.maxSize) {
                this.first = 0;
            }
        }
        return r;
    }
    
    public List<E> asList() {
        final List<E> tList = new ArrayList<E>();
        for (int i = 0; i < this.length(); ++i) {
            tList.add(this.get(i));
        }
        return tList;
    }
    
    public int length() {
        return this.numElems;
    }
    
    public void resize(final int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException("Negative array size [" + newSize + "] not allowed.");
        }
        if (newSize == this.numElems) {
            return;
        }
        final E[] temp = (E[])new Object[newSize];
        final int loopLen = (newSize < this.numElems) ? newSize : this.numElems;
        for (int i = 0; i < loopLen; ++i) {
            temp[i] = (E)this.ea[this.first];
            this.ea[this.first] = null;
            if (++this.first == this.numElems) {
                this.first = 0;
            }
        }
        this.ea = temp;
        this.first = 0;
        if ((this.numElems = loopLen) == (this.maxSize = newSize)) {
            this.last = 0;
        }
        else {
            this.last = loopLen;
        }
    }
}
