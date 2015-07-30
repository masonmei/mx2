// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.trace;

import java.util.List;
import com.newrelic.agent.TransactionData;

public interface ITransactionSampler
{
    boolean noticeTransaction(TransactionData p0);
    
    List<TransactionTrace> harvest(String p0);
    
    void stop();
    
    long getMaxDurationInNanos();
}
