// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.instrumentation.pointcuts.FieldAccessor;

public interface PreparedStatementExtension
{
    @FieldAccessor(fieldName = "sqlParameters")
    void _nr_setSqlParameters(Object[] p0);
    
    @FieldAccessor(fieldName = "sqlParameters")
    Object[] _nr_getSqlParameters();
    
    @FieldAccessor(fieldName = "statementData")
    StatementData _nr_getStatementData();
    
    @FieldAccessor(fieldName = "statementData")
    void _nr_setStatementData(StatementData p0);
}
