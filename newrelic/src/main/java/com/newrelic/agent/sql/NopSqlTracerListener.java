// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import java.util.Collections;
import java.util.List;
import com.newrelic.agent.instrumentation.pointcuts.database.SqlStatementTracer;

public class NopSqlTracerListener implements SqlTracerListener
{
    public void noticeSqlTracer(final SqlStatementTracer sqlTracer) {
    }
    
    public List<SqlStatementInfo> getSqlStatementInfo() {
        return Collections.emptyList();
    }
}
