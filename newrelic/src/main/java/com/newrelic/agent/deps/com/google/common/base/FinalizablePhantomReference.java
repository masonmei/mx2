// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.base;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.PhantomReference;

public abstract class FinalizablePhantomReference<T> extends PhantomReference<T> implements FinalizableReference
{
    protected FinalizablePhantomReference(final T referent, final FinalizableReferenceQueue queue) {
        super(referent, queue.queue);
        queue.cleanUp();
    }
}
