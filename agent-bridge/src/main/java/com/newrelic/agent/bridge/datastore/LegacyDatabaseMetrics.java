// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge.datastore;

import java.text.MessageFormat;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.bridge.Transaction;

public class LegacyDatabaseMetrics
{
    public static final String METRIC_NAMESPACE = "Database";
    public static final String ALL = "Database/all";
    public static final String ALL_WEB = "Database/allWeb";
    public static final String ALL_OTHER = "Database/allOther";
    public static final String STATEMENT = "Database/{0}/{1}";
    public static final String OPERATION = "Database/{0}";
    
    public static void doDatabaseMetrics(final Transaction tx, final TracedMethod method, final String table, final String operation) {
        method.setMetricName(new String[] { MessageFormat.format("Database/{0}/{1}", table, operation) });
        method.addRollupMetricName(new String[] { MessageFormat.format("Database/{0}/{1}", table, operation) });
        method.addRollupMetricName(new String[] { MessageFormat.format("Database/{0}", operation) });
        method.addRollupMetricName(new String[] { "Database/all" });
        if (tx.isWebTransaction()) {
            method.addRollupMetricName(new String[] { "Database/allWeb" });
        }
        else {
            method.addRollupMetricName(new String[] { "Database/allOther" });
        }
    }
}
