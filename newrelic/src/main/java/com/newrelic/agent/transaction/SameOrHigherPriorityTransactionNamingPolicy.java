// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.Transaction;

class SameOrHigherPriorityTransactionNamingPolicy extends TransactionNamingPolicy
{
    public boolean canSetTransactionName(final Transaction tx, final TransactionNamePriority priority) {
        if (priority == null) {
            return false;
        }
        final PriorityTransactionName ptn = tx.getPriorityTransactionName();
        return priority.compareTo((Enum)ptn.getPriority()) >= 0;
    }
}
