// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import com.newrelic.agent.tracers.Tracer;
import java.util.List;

public class TracerList implements List<Tracer>
{
    private final Set<TransactionActivity> activities;
    private List<Tracer> tracers;
    private final Tracer txRootTracer;
    
    public TracerList(final Tracer txRootTracer, final Set<TransactionActivity> activities) {
        if (activities == null) {
            throw new IllegalArgumentException();
        }
        this.activities = activities;
        this.txRootTracer = txRootTracer;
    }
    
    private List<Tracer> getTracers() {
        if (this.tracers == null) {
            int n = 0;
            for (final TransactionActivity txa : this.activities) {
                n += txa.getTracers().size();
            }
            ++n;
            this.tracers = new ArrayList<Tracer>(n);
            for (final TransactionActivity txa : this.activities) {
                if (txa.getRootTracer() != this.txRootTracer) {
                    this.tracers.add(txa.getRootTracer());
                }
                this.tracers.addAll(txa.getTracers());
            }
        }
        return this.tracers;
    }
    
    public int size() {
        return this.getTracers().size();
    }
    
    public boolean isEmpty() {
        return this.getTracers().isEmpty();
    }
    
    public boolean contains(final Object o) {
        return this.getTracers().contains(o);
    }
    
    public Iterator<Tracer> iterator() {
        return this.getTracers().iterator();
    }
    
    public Object[] toArray() {
        return this.getTracers().toArray();
    }
    
    public <T> T[] toArray(final T[] a) {
        return this.getTracers().toArray(a);
    }
    
    public boolean add(final Tracer e) {
        throw new UnsupportedOperationException();
    }
    
    public boolean remove(final Object o) {
        return this.getTracers().remove(o);
    }
    
    public boolean containsAll(final Collection<?> c) {
        return this.getTracers().containsAll(c);
    }
    
    public boolean addAll(final Collection<? extends Tracer> c) {
        return this.getTracers().addAll(c);
    }
    
    public boolean addAll(final int index, final Collection<? extends Tracer> c) {
        return this.getTracers().addAll(index, c);
    }
    
    public boolean removeAll(final Collection<?> c) {
        return this.getTracers().removeAll(c);
    }
    
    public boolean retainAll(final Collection<?> c) {
        return this.getTracers().retainAll(c);
    }
    
    public void clear() {
        this.getTracers().clear();
    }
    
    public Tracer get(final int index) {
        return this.getTracers().get(index);
    }
    
    public Tracer set(final int index, final Tracer element) {
        return this.getTracers().set(index, element);
    }
    
    public void add(final int index, final Tracer element) {
        this.getTracers().add(index, element);
    }
    
    public Tracer remove(final int index) {
        return this.getTracers().remove(index);
    }
    
    public int indexOf(final Object o) {
        return this.getTracers().indexOf(o);
    }
    
    public int lastIndexOf(final Object o) {
        return this.getTracers().lastIndexOf(o);
    }
    
    public ListIterator<Tracer> listIterator() {
        return this.getTracers().listIterator();
    }
    
    public ListIterator<Tracer> listIterator(final int index) {
        return this.getTracers().listIterator(index);
    }
    
    public List<Tracer> subList(final int fromIndex, final int toIndex) {
        return this.getTracers().subList(fromIndex, toIndex);
    }
}
