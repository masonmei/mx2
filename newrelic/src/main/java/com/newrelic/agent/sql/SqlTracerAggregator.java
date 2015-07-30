// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import com.newrelic.agent.TransactionData;
import java.util.List;

public interface SqlTracerAggregator
{
    List<SqlTrace> getAndClearSqlTracers();
    
    void addSqlTracers(TransactionData p0);
}
