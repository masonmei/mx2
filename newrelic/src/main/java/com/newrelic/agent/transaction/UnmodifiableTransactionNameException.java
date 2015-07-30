// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

public class UnmodifiableTransactionNameException extends Exception
{
    private static final long serialVersionUID = 2277591207140681026L;
    
    public UnmodifiableTransactionNameException(final Exception ex) {
        super(ex);
    }
}
