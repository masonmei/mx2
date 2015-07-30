// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import com.newrelic.agent.util.Strings;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.Transaction;

public abstract class TransactionNamingPolicy
{
    private static final HigherPriorityTransactionNamingPolicy HIGHER_PRIORITY_INSTANCE;
    private static final SameOrHigherPriorityTransactionNamingPolicy SAME_OR_HIGHER_INSTANCE;
    
    public final boolean setTransactionName(final Transaction tx, final String name, final String category, final TransactionNamePriority priority) {
        return tx.conditionalSetPriorityTransactionName(this, name, category, priority);
    }
    
    public abstract boolean canSetTransactionName(final Transaction p0, final TransactionNamePriority p1);
    
    public PriorityTransactionName getPriorityTransactionName(final Transaction tx, String name, final String category, final TransactionNamePriority priority) {
        if (category == null) {
            return PriorityTransactionName.create(name, category, priority);
        }
        if (name == null) {
            return PriorityTransactionName.create(name, category, priority);
        }
        final String txType = tx.isWebTransaction() ? "WebTransaction" : "OtherTransaction";
        if (!Strings.isEmpty(name)) {
            if (name.startsWith(txType)) {
                return PriorityTransactionName.create(name, category, priority);
            }
            if (!name.startsWith("/")) {
                name = '/' + name;
            }
        }
        if (category.length() > 0) {
            name = '/' + category + name;
        }
        return PriorityTransactionName.create(tx, name, category, priority);
    }
    
    public static TransactionNamingPolicy getSameOrHigherPriorityTransactionNamingPolicy() {
        return TransactionNamingPolicy.SAME_OR_HIGHER_INSTANCE;
    }
    
    public static TransactionNamingPolicy getHigherPriorityTransactionNamingPolicy() {
        return TransactionNamingPolicy.HIGHER_PRIORITY_INSTANCE;
    }
    
    static {
        HIGHER_PRIORITY_INSTANCE = new HigherPriorityTransactionNamingPolicy();
        SAME_OR_HIGHER_INSTANCE = new SameOrHigherPriorityTransactionNamingPolicy();
    }
}
