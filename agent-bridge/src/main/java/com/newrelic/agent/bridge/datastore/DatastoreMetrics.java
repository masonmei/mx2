// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge.datastore;

import java.util.HashMap;
import com.newrelic.api.agent.NewRelic;
import java.text.MessageFormat;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.agent.bridge.Transaction;
import java.util.Set;
import java.util.Map;

public class DatastoreMetrics
{
    public static final String METRIC_NAMESPACE = "Datastore";
    public static final String ALL = "Datastore/all";
    public static final String ALL_WEB = "Datastore/allWeb";
    public static final String ALL_OTHER = "Datastore/allOther";
    public static final String VENDOR_ALL = "Datastore/{0}/all";
    public static final String VENDOR_ALL_WEB = "Datastore/{0}/allWeb";
    public static final String VENDOR_ALL_OTHER = "Datastore/{0}/allOther";
    public static final String STATEMENT_METRIC = "Datastore/statement/{0}/{1}/{2}";
    public static final String OPERATION_METRIC = "Datastore/operation/{0}/{1}";
    public static final String INSTANCE_METRIC = "Datastore/instance/{0}/{1}:{2}/{3}";
    public static final String DEFAULT_OPERATION = "other";
    public static final String DEFAULT_TABLE = "other";
    private static Map<DatastoreVendor, DatastoreMetrics> instances;
    private final DatastoreVendor datastoreVendor;
    
    public static DatastoreMetrics getInstance(final DatastoreVendor datastoreVendor) {
        DatastoreMetrics instance = DatastoreMetrics.instances.get(datastoreVendor);
        if (null == instance) {
            synchronized (DatastoreMetrics.instances) {
                instance = DatastoreMetrics.instances.get(datastoreVendor);
                if (null == instance) {
                    instance = new DatastoreMetrics(datastoreVendor);
                    DatastoreMetrics.instances.put(datastoreVendor, instance);
                }
            }
        }
        return instance;
    }
    
    public static Set<DatastoreVendor> getInstanceNames() {
        return DatastoreMetrics.instances.keySet();
    }
    
    private DatastoreMetrics(final DatastoreVendor dbVendor) {
        this.datastoreVendor = dbVendor;
    }
    
    public void collectDatastoreMetrics(final Transaction tx, final TracedMethod method, final String table, final String operation, final String host, final String port) {
        if (null == table) {
            method.setMetricName(new String[] { MessageFormat.format("Datastore/operation/{0}/{1}", this.datastoreVendor, operation) });
        }
        else {
            method.addRollupMetricName(new String[] { MessageFormat.format("Datastore/operation/{0}/{1}", this.datastoreVendor, operation) });
            method.setMetricName(new String[] { MessageFormat.format("Datastore/statement/{0}/{1}/{2}", this.datastoreVendor, table, operation) });
        }
        method.addRollupMetricName(new String[] { "Datastore/all" });
        method.addRollupMetricName(new String[] { MessageFormat.format("Datastore/{0}/all", this.datastoreVendor) });
        if (tx.isWebTransaction()) {
            method.addRollupMetricName(new String[] { "Datastore/allWeb" });
            method.addRollupMetricName(new String[] { MessageFormat.format("Datastore/{0}/allWeb", this.datastoreVendor) });
        }
        else {
            method.addRollupMetricName(new String[] { "Datastore/allOther" });
            method.addRollupMetricName(new String[] { MessageFormat.format("Datastore/{0}/allOther", this.datastoreVendor) });
        }
    }
    
    public void unparsedQuerySupportability() {
        NewRelic.incrementCounter(MessageFormat.format("Supportability/Datastore/{0}/unparsedQuery", this.datastoreVendor));
    }
    
    static {
        DatastoreMetrics.instances = new HashMap<DatastoreVendor, DatastoreMetrics>(DatastoreVendor.values().length);
    }
}
