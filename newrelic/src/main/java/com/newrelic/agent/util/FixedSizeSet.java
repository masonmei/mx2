// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.TreeSet;

public class FixedSizeSet<E> extends TreeSet<E>
{
    private static final long serialVersionUID = 1474558437021809591L;
    private int size;
    
    public FixedSizeSet(final int size) {
        this.size = size;
    }
    
    public int getFixedSize() {
        return this.size;
    }
    
    public boolean add(final E o) {
        if (this.size() >= this.size) {
            final E first = this.first();
            final int comparison = (o instanceof Comparable) ? ((Comparable)o).compareTo(first) : this.comparator().compare((Object)o, (Object)first);
            if (comparison < 0) {
                return false;
            }
            super.remove(first);
        }
        return super.add(o);
    }
}
