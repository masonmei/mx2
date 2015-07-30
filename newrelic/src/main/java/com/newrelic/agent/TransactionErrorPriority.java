// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.util.concurrent.atomic.AtomicReference;

public enum TransactionErrorPriority
{
    API {
        protected boolean updatePriority(final AtomicReference<TransactionErrorPriority> current) {
            return this != current.get() && (current.compareAndSet(TRACER, this) || current.compareAndSet(ASYNC_POINTCUT, this));
        }
    }, 
    TRACER {
        protected boolean updatePriority(final AtomicReference<TransactionErrorPriority> current) {
            return this == current.get() || current.compareAndSet(ASYNC_POINTCUT, this);
        }
    }, 
    ASYNC_POINTCUT {
        protected boolean updatePriority(final AtomicReference<TransactionErrorPriority> current) {
            return false;
        }
    };
    
    protected abstract boolean updatePriority(final AtomicReference<TransactionErrorPriority> p0);
    
    public boolean updateCurrentPriority(final AtomicReference<TransactionErrorPriority> current) {
        return current.compareAndSet(null, this) || this.updatePriority(current);
    }
}
