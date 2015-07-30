// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.Transaction;

public abstract class AbstractTransactionNamer implements TransactionNamer
{
    private final Transaction tx;
    private final String uri;
    
    protected AbstractTransactionNamer(final Transaction tx, final String uri) {
        this.tx = tx;
        this.uri = uri;
    }
    
    protected final String getUri() {
        return this.uri;
    }
    
    protected final Transaction getTransaction() {
        return this.tx;
    }
    
    protected boolean canSetTransactionName() {
        return this.canSetTransactionName(TransactionNamePriority.REQUEST_URI);
    }
    
    protected boolean canSetTransactionName(final TransactionNamePriority priority) {
        if (this.tx == null || this.tx.isIgnore()) {
            return false;
        }
        final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
        return policy.canSetTransactionName(this.tx, priority);
    }
    
    protected void setTransactionName(final String name, final String category, final TransactionNamePriority priority) {
        if (this.canSetTransactionName(priority)) {
            final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
            policy.setTransactionName(this.tx, name, category, priority);
        }
    }
}
