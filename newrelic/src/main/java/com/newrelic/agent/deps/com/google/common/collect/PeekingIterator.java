// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public interface PeekingIterator<E> extends Iterator<E>
{
    E peek();
    
    E next();
    
    void remove();
}
