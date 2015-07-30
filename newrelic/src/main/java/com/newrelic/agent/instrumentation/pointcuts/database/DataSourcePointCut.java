// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import java.sql.SQLException;
import com.newrelic.agent.database.DatabaseVendor;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.tracers.DatabaseTracer;
import com.newrelic.agent.tracers.DefaultTracer;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import java.sql.Connection;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import javax.sql.DataSource;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class DataSourcePointCut extends TracerFactoryPointCut
{
    public DataSourcePointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("jdbc_data_source"), new InterfaceMatcher(Type.getInternalName(DataSource.class)), PointCut.createMethodMatcher(new ExactMethodMatcher("getConnection", "()" + Type.getDescriptor(Connection.class)), new ExactMethodMatcher("getConnection", "(Ljava/lang/String;Ljava/lang/String;)" + Type.getDescriptor(Connection.class))));
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    private boolean explainsEnabled(final Transaction tx) {
        final TransactionTracerConfig ttConfig = tx.getTransactionTracerConfig();
        return ttConfig.isEnabled() && ttConfig.isExplainEnabled();
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object dataSource, final Object[] args) {
        final Tracer parentTracer = transaction.getTransactionActivity().getLastTracer();
        if (!this.explainsEnabled(transaction) || transaction.getTransactionCounts().isOverTracerSegmentLimit()) {
            if (parentTracer != null) {
                final ClassMethodSignature parentSig = parentTracer.getClassMethodSignature();
                if (parentSig.getClassName().equals(sig.getClassName()) && parentSig.getMethodName().equals(sig.getMethodName())) {
                    return null;
                }
            }
            final ClassMethodMetricNameFormat metricName = new ClassMethodMetricNameFormat(sig, dataSource);
            return new ConnectionTracer(transaction, sig, dataSource, metricName);
        }
        if (parentTracer instanceof DataSourceTracer) {
            return null;
        }
        final ClassMethodMetricNameFormat metricName = new ClassMethodMetricNameFormat(sig, dataSource);
        DataSource ds;
        if (dataSource instanceof DataSource) {
            ds = (DataSource)dataSource;
        }
        else {
            try {
                ds = new ReflectiveDataSource(dataSource);
            }
            catch (ClassNotFoundException e) {
                if (Agent.LOG.isLoggable(Level.SEVERE)) {
                    Agent.LOG.severe(dataSource.getClass().getName() + " does not appear to be an instance of DataSource");
                }
                return new ConnectionTracer(transaction, sig, dataSource, metricName);
            }
        }
        return new DataSourceTracer(transaction, sig, ds, args, metricName);
    }
    
    private static class ConnectionTracer extends DefaultTracer implements DatabaseTracer
    {
        private Throwable throwable;
        
        public ConnectionTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final MetricNameFormat metricNameFormatter) {
            super(transaction, sig, object, metricNameFormatter);
        }
        
        public void finish(final Throwable pThrowable) {
            super.finish(this.throwable = pThrowable);
        }
        
        protected void doRecordMetrics(final TransactionStats transactionStats) {
            transactionStats.getUnscopedStats().getResponseTimeStats("Datastore/getConnection").recordResponseTime(this.getExclusiveDuration(), TimeUnit.NANOSECONDS);
            if (this.throwable != null) {
                this.getTransactionActivity().getTransactionStats().getUnscopedStats().getStats("DatastoreErrors/all").incrementCallCount();
            }
        }
    }
    
    private static class DataSourceTracer extends ConnectionTracer
    {
        private AConnectionFactory connectionFactory;
        
        public DataSourceTracer(final Transaction transaction, final ClassMethodSignature sig, final DataSource dataSource, final Object[] args, final ClassMethodMetricNameFormat metricName) {
            super(transaction, sig, dataSource, metricName);
            if (args.length == 2) {
                this.connectionFactory = new DataSourceConnectionFactory(dataSource, (String)args[0], (String)args[1]);
            }
            else {
                this.connectionFactory = new NoArgsDataSourceConnectionFactory(dataSource);
            }
        }
        
        protected void doFinish(final int opcode, final Object connection) {
            if (connection != null) {
                final DatabaseVendor databaseVendor = DatabaseUtils.getDatabaseVendor((Connection)connection);
                this.connectionFactory.setDatabaseVendor(databaseVendor);
                SqlDriverPointCut.putConnectionFactory(this.getTransaction(), (Connection)connection, this.connectionFactory);
            }
        }
    }
    
    private static class NoArgsDataSourceConnectionFactory extends AConnectionFactory
    {
        public NoArgsDataSourceConnectionFactory(final DataSource dataSource) {
            super(dataSource);
        }
        
        public Connection getConnection() throws SQLException {
            return this.getDataSource().getConnection();
        }
    }
    
    private static class DataSourceConnectionFactory extends AConnectionFactory
    {
        private final String password;
        private final String username;
        
        public DataSourceConnectionFactory(final DataSource dataSource, final String username, final String password) {
            super(dataSource);
            this.username = username;
            this.password = password;
        }
        
        public Connection getConnection() throws SQLException {
            return this.getDataSource().getConnection(this.username, this.password);
        }
    }
    
    private abstract static class AConnectionFactory implements ConnectionFactory
    {
        private String url;
        private final DataSource dataSource;
        private DatabaseVendor databaseVendor;
        
        public AConnectionFactory(final DataSource dataSource) {
            this.databaseVendor = DatabaseVendor.UNKNOWN;
            this.dataSource = dataSource;
        }
        
        public String getUrl() {
            return this.url;
        }
        
        public void setUrl(final String url) {
            this.url = url;
        }
        
        protected DataSource getDataSource() {
            return this.dataSource;
        }
        
        public DatabaseVendor getDatabaseVendor() {
            return this.databaseVendor;
        }
        
        void setDatabaseVendor(final DatabaseVendor databaseVendor) {
            this.databaseVendor = databaseVendor;
        }
    }
}
