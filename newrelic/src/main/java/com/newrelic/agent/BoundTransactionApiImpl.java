// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

public class BoundTransactionApiImpl extends TransactionApiImpl
{
    private final Transaction boundTransaction;
    
    public BoundTransactionApiImpl(final Transaction boundTransaction) {
        if (boundTransaction == null) {
            throw new IllegalArgumentException("boundTransaction must not be null");
        }
        this.boundTransaction = boundTransaction;
    }
    
    public Transaction getTransaction() {
        return this.boundTransaction;
    }
}
