// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import com.newrelic.agent.TransactionData;
import com.newrelic.agent.instrumentation.pointcuts.database.SqlStatementTracer;

class SqlTracerInfo
{
    private final SqlStatementTracer sqlTracer;
    private TransactionData transactionData;
    
    SqlTracerInfo(final TransactionData transactionData, final SqlStatementTracer sqlTracer) {
        this.transactionData = transactionData;
        this.sqlTracer = sqlTracer;
    }
    
    public TransactionData getTransactionData() {
        return this.transactionData;
    }
    
    public SqlStatementTracer getSqlTracer() {
        return this.sqlTracer;
    }
    
    public void setTransactionData(final TransactionData td) {
        this.transactionData = td;
    }
}
