// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.Transaction;

public class OtherTransactionNamer extends AbstractTransactionNamer
{
    private OtherTransactionNamer(final Transaction tx, final String dispatcherUri) {
        super(tx, dispatcherUri);
    }
    
    public void setTransactionName() {
        this.setTransactionName(this.getUri(), "", TransactionNamePriority.REQUEST_URI);
    }
    
    public static TransactionNamer create(final Transaction tx, final String dispatcherUri) {
        return new OtherTransactionNamer(tx, dispatcherUri);
    }
}
