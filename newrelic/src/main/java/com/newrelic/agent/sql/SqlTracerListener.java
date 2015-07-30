// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import java.util.List;
import com.newrelic.agent.instrumentation.pointcuts.database.SqlStatementTracer;

public interface SqlTracerListener
{
    void noticeSqlTracer(SqlStatementTracer p0);
    
    List<SqlStatementInfo> getSqlStatementInfo();
}
