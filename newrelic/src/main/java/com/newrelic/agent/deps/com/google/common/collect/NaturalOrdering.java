// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import com.newrelic.agent.deps.com.google.common.base.Preconditions;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class NaturalOrdering extends Ordering<Comparable> implements Serializable
{
    static final NaturalOrdering INSTANCE;
    private static final long serialVersionUID = 0L;
    
    @Override
    public int compare(final Comparable left, final Comparable right) {
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
        return left.compareTo(right);
    }
    
    @Override
    public <S extends Comparable> Ordering<S> reverse() {
        return (Ordering<S>)ReverseNaturalOrdering.INSTANCE;
    }
    
    private Object readResolve() {
        return NaturalOrdering.INSTANCE;
    }
    
    @Override
    public String toString() {
        return "Ordering.natural()";
    }
    
    static {
        INSTANCE = new NaturalOrdering();
    }
}
