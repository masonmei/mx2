// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.Arrays;

public class InsertOnlyArray<E>
{
    private volatile Object[] elements;
    private int size;
    
    public InsertOnlyArray(final int capacity) {
        this.size = 0;
        this.elements = new Object[capacity];
    }
    
    public E get(final int index) {
        return (E)this.elements[index];
    }
    
    public int getIndex(final E element) {
        final Object[] arr = this.elements;
        for (int i = 0; i < arr.length; ++i) {
            if (element.equals(arr[i])) {
                return i;
            }
        }
        return -1;
    }
    
    public synchronized int add(final E newElement) {
        final int position = this.size;
        if (this.size + 1 > this.elements.length) {
            this.grow(this.size + 1);
        }
        this.elements[position] = newElement;
        ++this.size;
        return position;
    }
    
    private void grow(final int minCapacity) {
        final int oldCapacity = this.elements.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        newCapacity = Math.max(newCapacity, minCapacity);
        this.elements = Arrays.copyOf(this.elements, newCapacity);
    }
}
