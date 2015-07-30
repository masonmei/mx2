// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import com.newrelic.agent.bridge.TransactionNamePriority;

public class TransactionName
{
    public static final TransactionName CUSTOM_DEFAULT;
    public static final TransactionName BUILT_IN_DEFAULT;
    final String category;
    final String path;
    final TransactionNamePriority transactionNamePriority;
    final boolean override;
    
    public TransactionName(final TransactionNamePriority namingPriority, final boolean override, final String category, final String path) {
        this.category = category;
        this.path = path;
        this.override = override;
        this.transactionNamePriority = namingPriority;
    }
    
    private TransactionName(final TransactionNamePriority priority) {
        this(priority, false, null, null);
    }
    
    public static boolean isSimpleTransactionName(final TransactionName transactionName) {
        return TransactionName.BUILT_IN_DEFAULT.equals(transactionName) || TransactionName.CUSTOM_DEFAULT.equals(transactionName);
    }
    
    static {
        CUSTOM_DEFAULT = new TransactionName(TransactionNamePriority.CUSTOM_HIGH);
        BUILT_IN_DEFAULT = new TransactionName(TransactionNamePriority.FRAMEWORK_HIGH);
    }
}
