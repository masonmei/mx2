// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.values;

import com.newrelic.agent.jmx.metrics.JtaJmxMetricGenerator;
import com.newrelic.agent.jmx.create.JmxMetricModifier;
import com.newrelic.agent.jmx.metrics.EjbTransactionJmxMetricGenerator;
import com.newrelic.agent.jmx.metrics.JMXMetricType;
import com.newrelic.agent.jmx.metrics.EjbPoolJmxMetricGenerator;
import com.newrelic.agent.jmx.metrics.DataSourceJmxMetricGenerator;
import com.newrelic.agent.jmx.metrics.ServerJmxMetricGenerator;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.ArrayList;
import com.newrelic.agent.jmx.create.JmxAttributeFilter;
import com.newrelic.agent.jmx.metrics.BaseJmxValue;
import java.util.List;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;

public class WeblogicJmxValues extends JmxFrameworkValues
{
    private static String PREFIX;
    private static final int METRIC_COUNT = 1;
    private static final List<BaseJmxValue> METRICS;
    private static final JmxAttributeFilter FILTER;
    
    public List<BaseJmxValue> getFrameworkMetrics() {
        return WeblogicJmxValues.METRICS;
    }
    
    public String getPrefix() {
        return WeblogicJmxValues.PREFIX;
    }
    
    static {
        WeblogicJmxValues.PREFIX = "com.bea";
        METRICS = new ArrayList<BaseJmxValue>(1);
        FILTER = new JmxAttributeFilter() {
            public boolean keepMetric(final String rootMetricName) {
                if (rootMetricName.contains("/uuid-")) {
                    Agent.LOG.log(Level.FINER, "Weblogic JMX metric {0} is being ignored because it appears to be an instance.", new Object[] { rootMetricName });
                    return false;
                }
                return true;
            }
        };
        WeblogicJmxValues.METRICS.add(new BaseJmxValue("com.bea:ServerRuntime=*,Name=ThreadPoolRuntime,Type=ThreadPoolRuntime", "JmxBuiltIn/ThreadPool/{Name}/", new JmxMetric[] { ServerJmxMetricGenerator.ACTIVE_THREAD_POOL_COUNT.createMetric("HoggingThreadCount"), ServerJmxMetricGenerator.IDLE_THREAD_POOL_COUNT.createMetric("ExecuteThreadIdleCount"), ServerJmxMetricGenerator.STANDBY_THREAD_POOL_COUNT.createMetric("StandbyThreadCount") }));
        WeblogicJmxValues.METRICS.add(new BaseJmxValue("com.bea:ServerRuntime=*,Name=*,Type=JDBCDataSourceRuntime", "JmxBuiltIn/DataSources/{Name}/", new JmxMetric[] { DataSourceJmxMetricGenerator.CONNECTIONS_AVAILABLE.createMetric("NumAvailable"), DataSourceJmxMetricGenerator.CONNECTIONS_POOL_SIZE.createMetric("CurrCapacity"), DataSourceJmxMetricGenerator.CONNECTIONS_CREATED.createMetric("ConnectionsTotalCount"), DataSourceJmxMetricGenerator.CONNECTIONS_ACTIVE.createMetric("ActiveConnectionsCurrentCount"), DataSourceJmxMetricGenerator.CONNECTIONS_LEAKED.createMetric("LeakedConnectionCount"), DataSourceJmxMetricGenerator.CONNECTIONS_CACHE_SIZE.createMetric("PrepStmtCacheCurrentSize"), DataSourceJmxMetricGenerator.CONNECTION_REQUEST_WAITING_COUNT.createMetric("WaitingForConnectionCurrentCount"), DataSourceJmxMetricGenerator.CONNECTION_REQUEST_TOTAL_COUNT.createMetric("WaitingForConnectionTotal"), DataSourceJmxMetricGenerator.CONNECTION_REQUEST_SUCCESS.createMetric("WaitingForConnectionSuccessTotal"), DataSourceJmxMetricGenerator.CONNECTION_REQUEST_FAILURE.createMetric("WaitingForConnectionFailureTotal") }));
        WeblogicJmxValues.METRICS.add(new BaseJmxValue("com.bea:ServerRuntime=*,Name=*,ApplicationRuntime=*,Type=EJBPoolRuntime,EJBComponentRuntime=*,*", "JmxBuiltIn/EJB/Pool/Bean/{ApplicationRuntime}/{EJBComponentRuntime}/{Name}/", WeblogicJmxValues.FILTER, new JmxMetric[] { EjbPoolJmxMetricGenerator.SUCCESS.createMetric("AccessTotalCount", "MissTotalCount"), EjbPoolJmxMetricGenerator.FAILURE.createMetric("MissTotalCount"), EjbPoolJmxMetricGenerator.THREADS_WAITING.createMetric("WaiterCurrentCount"), EjbPoolJmxMetricGenerator.DESTROY.createMetric("DestroyedTotalCount"), EjbPoolJmxMetricGenerator.ACTIVE.createMetric("BeansInUseCurrentCount"), EjbPoolJmxMetricGenerator.AVAILABLE.createMetric("PooledBeansCurrentCount") }));
        WeblogicJmxValues.METRICS.add(new BaseJmxValue("com.bea:ServerRuntime=*,Name=*,ApplicationRuntime=*,Type=EJBTransactionRuntime,EJBComponentRuntime=*,*", "JmxBuiltIn/EJB/Transactions/Application/{ApplicationRuntime}/", WeblogicJmxValues.FILTER, null, JMXMetricType.SUM_ALL_BEANS, new JmxMetric[] { EjbTransactionJmxMetricGenerator.COUNT.createMetric("TransactionsCommittedTotalCount", "TransactionsRolledBackTotalCount", "TransactionsTimedOutTotalCount"), EjbTransactionJmxMetricGenerator.COMMIT.createMetric("TransactionsCommittedTotalCount"), EjbTransactionJmxMetricGenerator.ROLLBACK.createMetric("TransactionsRolledBackTotalCount"), EjbTransactionJmxMetricGenerator.TIMEOUT.createMetric("TransactionsTimedOutTotalCount") }));
        WeblogicJmxValues.METRICS.add(new BaseJmxValue("com.bea:ServerRuntime=*,Name=*,ApplicationRuntime=*,Type=EJBTransactionRuntime,EJBComponentRuntime=*,*", "JmxBuiltIn/EJB/Transactions/Module/{ApplicationRuntime}/{EJBComponentRuntime}/", WeblogicJmxValues.FILTER, null, JMXMetricType.SUM_ALL_BEANS, new JmxMetric[] { EjbTransactionJmxMetricGenerator.COUNT.createMetric("TransactionsCommittedTotalCount", "TransactionsRolledBackTotalCount", "TransactionsTimedOutTotalCount"), EjbTransactionJmxMetricGenerator.COMMIT.createMetric("TransactionsCommittedTotalCount"), EjbTransactionJmxMetricGenerator.ROLLBACK.createMetric("TransactionsRolledBackTotalCount"), EjbTransactionJmxMetricGenerator.TIMEOUT.createMetric("TransactionsTimedOutTotalCount") }));
        WeblogicJmxValues.METRICS.add(new BaseJmxValue("com.bea:ServerRuntime=*,Name=*,ApplicationRuntime=*,Type=EJBTransactionRuntime,EJBComponentRuntime=*,*", "JmxBuiltIn/EJB/Transactions/Bean/{ApplicationRuntime}/{EJBComponentRuntime}/{Name}/", WeblogicJmxValues.FILTER, new JmxMetric[] { EjbTransactionJmxMetricGenerator.COUNT.createMetric("TransactionsCommittedTotalCount", "TransactionsRolledBackTotalCount", "TransactionsTimedOutTotalCount"), EjbTransactionJmxMetricGenerator.COMMIT.createMetric("TransactionsCommittedTotalCount"), EjbTransactionJmxMetricGenerator.ROLLBACK.createMetric("TransactionsRolledBackTotalCount"), EjbTransactionJmxMetricGenerator.TIMEOUT.createMetric("TransactionsTimedOutTotalCount") }));
        WeblogicJmxValues.METRICS.add(new BaseJmxValue("com.bea:ServerRuntime=*,Name=JTARuntime,Type=JTARuntime", "JmxBuiltIn/JTA/{Name}/", WeblogicJmxValues.FILTER, new JmxMetric[] { JtaJmxMetricGenerator.COUNT.createMetric("TransactionTotalCount"), JtaJmxMetricGenerator.COMMIT.createMetric("TransactionCommittedTotalCount"), JtaJmxMetricGenerator.ROLLBACK.createMetric("TransactionRolledBackTotalCount"), JtaJmxMetricGenerator.ABANDONDED.createMetric("TransactionAbandonedTotalCount") }));
    }
}
