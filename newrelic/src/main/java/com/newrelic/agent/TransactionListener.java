// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.stats.TransactionStats;

public interface TransactionListener
{
    void dispatcherTransactionFinished(TransactionData p0, TransactionStats p1);
}
