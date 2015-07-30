// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import com.newrelic.agent.Transaction;
import java.text.MessageFormat;
import com.newrelic.agent.bridge.TransactionNamePriority;

public class PriorityTransactionName
{
    public static final PriorityTransactionName NONE;
    public static final String WEB_TRANSACTION_CATEGORY = "Web";
    public static final String UNDEFINED_TRANSACTION_CATEGORY = "Other";
    private final TransactionNamePriority priority;
    private final String prefix;
    private final String partialName;
    private final String category;
    
    private PriorityTransactionName(final String prefix, final String partialName, final String category, final TransactionNamePriority priority) {
        this.prefix = prefix;
        this.partialName = partialName;
        this.category = category;
        this.priority = priority;
    }
    
    private String initializeName(final String partialName) {
        if (this.getPrefix() == null) {
            return null;
        }
        if (partialName == null) {
            return this.getPrefix();
        }
        return this.getPrefix() + partialName;
    }
    
    public String getName() {
        return this.initializeName(this.partialName);
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getPartialName() {
        return this.partialName;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public boolean isFrozen() {
        return this.priority == TransactionNamePriority.FROZEN;
    }
    
    public PriorityTransactionName freeze() {
        if (this.isFrozen()) {
            return this;
        }
        return create(this.getPrefix(), this.getPartialName(), this.category, TransactionNamePriority.FROZEN);
    }
    
    public TransactionNamePriority getPriority() {
        return this.priority;
    }
    
    public String toString() {
        return MessageFormat.format("{0}[name={1}, priority={2}]", this.getClass().getName(), this.getName(), this.getPriority());
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.partialName == null) ? 0 : this.partialName.hashCode());
        result = 31 * result + ((this.prefix == null) ? 0 : this.prefix.hashCode());
        result = 31 * result + ((this.priority == null) ? 0 : this.priority.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PriorityTransactionName)) {
            return false;
        }
        final PriorityTransactionName other = (PriorityTransactionName)obj;
        final String name = this.getName();
        final String otherName = other.getName();
        if (name == null) {
            if (otherName != null) {
                return false;
            }
        }
        else if (!name.equals(otherName)) {
            return false;
        }
        return this.priority.equals((Object)other.priority);
    }
    
    public static PriorityTransactionName create(final String transactionName, final String category, final TransactionNamePriority priority) {
        if (transactionName == null) {
            return new PriorityTransactionName(null, null, category, priority);
        }
        int index = transactionName.indexOf(47, 1);
        if (index > 0) {
            index = transactionName.indexOf(47, index + 1);
            if (index > 0) {
                final String prefix = transactionName.substring(0, index);
                final String partialName = transactionName.substring(index);
                return new PriorityTransactionName(prefix, partialName, category, priority);
            }
        }
        return new PriorityTransactionName(transactionName, null, category, priority);
    }
    
    public static PriorityTransactionName create(final Transaction tx, final String partialName, String category, final TransactionNamePriority priority) {
        if (priority == null) {
            return null;
        }
        if (category == null || category.isEmpty()) {
            category = (tx.isWebTransaction() ? "Web" : "Other");
        }
        return new PriorityTransactionName(null, partialName, category, priority) {
            public String getPrefix() {
                return tx.isWebTransaction() ? "WebTransaction" : "OtherTransaction";
            }
        };
    }
    
    public static PriorityTransactionName create(final String prefix, final String partialName, final String category, final TransactionNamePriority priority) {
        if (priority == null) {
            return null;
        }
        return new PriorityTransactionName(prefix, partialName, category, priority);
    }
    
    static {
        NONE = create(null, null, TransactionNamePriority.NONE);
    }
}
