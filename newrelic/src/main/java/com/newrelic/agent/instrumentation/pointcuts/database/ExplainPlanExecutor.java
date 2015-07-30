// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import java.sql.SQLException;
import com.newrelic.agent.database.DatabaseVendor;
import java.sql.Connection;
import com.newrelic.agent.database.DatabaseService;

public interface ExplainPlanExecutor
{
    void runExplainPlan(DatabaseService p0, Connection p1, DatabaseVendor p2) throws SQLException;
}
