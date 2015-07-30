// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.sql.SQLException;
import com.newrelic.agent.database.DatabaseVendor;
import java.util.Properties;
import java.sql.Driver;
import com.newrelic.agent.tracers.DatabaseTracer;
import com.newrelic.agent.tracers.MethodExitTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.cache.Cache;
import java.util.Map;
import com.newrelic.agent.Agent;
import java.sql.Connection;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class SqlDriverPointCut extends TracerFactoryPointCut
{
    public SqlDriverPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("jdbc_driver"), new InterfaceMatcher("java/sql/Driver"), PointCut.createExactMethodMatcher("connect", "(Ljava/lang/String;Ljava/util/Properties;)Ljava/sql/Connection;"));
    }
    
    public static final void putConnectionFactory(final Transaction tx, final Connection connection, final ConnectionFactory factory) {
        final Connection innerConnection = DatabaseUtils.getInnerConnection(connection);
        if (innerConnection instanceof ConnectionExtension && ((ConnectionExtension)innerConnection)._nr_getConnectionFactory() != null) {
            return;
        }
        if (Agent.isDebugEnabled()) {
            Agent.LOG.finer("Tracking connection: " + connection.getClass().getName());
        }
        if (connection instanceof ConnectionExtension) {
            ((ConnectionExtension)connection)._nr_setConnectionFactory(factory);
            return;
        }
        if (factory.getDatabaseVendor().isExplainPlanSupported() && tx.getTransactionTracerConfig().isEnabled() && tx.getTransactionTracerConfig().isExplainEnabled()) {
            tx.getConnectionCache().putConnectionFactory(connection, factory);
        }
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    public static ConnectionFactory getConnectionFactory(final Transaction transaction, Connection connection) {
        connection = DatabaseUtils.getInnerConnection(connection);
        if (connection instanceof ConnectionExtension) {
            final ConnectionFactory factory = ((ConnectionExtension)connection)._nr_getConnectionFactory();
            if (factory != null) {
                return factory;
            }
        }
        final Cache<Connection, ConnectionFactory> connections = transaction.getConnectionCache().getConnectionFactoryCache();
        if (connections == null) {
            return null;
        }
        final ConnectionFactory connectionFactory = connections.getIfPresent(connection);
        if (connectionFactory == null) {
            if (connections.size() == 1L) {
                return connections.asMap().values().iterator().next();
            }
            if (connections.size() < 100L) {
                for (final Map.Entry<Connection, ConnectionFactory> entry : connections.asMap().entrySet()) {
                    if (connection.equals(entry.getKey()) || entry.getKey().equals(connection)) {
                        connections.put(connection, entry.getValue());
                        return entry.getValue();
                    }
                }
            }
        }
        return connectionFactory;
    }
    
    public Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, final Object driver, final Object[] args) {
        return tx.getTransactionCounts().isOverTracerSegmentLimit() ? null : new ConnectionTracer(tx, sig, driver, args);
    }
    
    private static class ConnectionErrorTracer extends MethodExitTracer implements DatabaseTracer
    {
        public ConnectionErrorTracer(final ClassMethodSignature signature, final Transaction transaction) {
            super(signature, transaction);
        }
        
        public void finish(final Throwable throwable) {
            super.finish(throwable);
            this.getTransactionActivity().getTransactionStats().getUnscopedStats().getStats("DatastoreErrors/all").incrementCallCount();
        }
        
        protected void doFinish(final int opcode, final Object returnValue) {
        }
    }
    
    private static class ConnectionTracer extends ConnectionErrorTracer
    {
        private final DriverConnectionFactory connectionFactory;
        
        public ConnectionTracer(final Transaction transaction, final ClassMethodSignature sig, final Object driver, final Object[] args) {
            super(sig, transaction);
            this.connectionFactory = new DriverConnectionFactory((Driver)driver, (String)args[0], (Properties)args[1]);
        }
        
        protected void doFinish(final int opcode, final Object connection) {
            super.doFinish(opcode, connection);
            if (connection != null) {
                this.connectionFactory.setDatabaseVendor(DatabaseUtils.getDatabaseVendor((Connection)connection));
                SqlDriverPointCut.putConnectionFactory(this.getTransaction(), (Connection)connection, this.connectionFactory);
            }
        }
    }
    
    private static class DriverConnectionFactory implements ConnectionFactory
    {
        private static final Properties EMPTY_PROPERTIES;
        private final Driver driver;
        private final String url;
        private final Properties props;
        private DatabaseVendor databaseVendor;
        
        public DriverConnectionFactory(final Driver driver, final String url, final Properties props) {
            this.databaseVendor = DatabaseVendor.UNKNOWN;
            this.driver = driver;
            this.url = url;
            this.props = ((props == null || props.isEmpty()) ? DriverConnectionFactory.EMPTY_PROPERTIES : props);
        }
        
        public Connection getConnection() throws SQLException {
            try {
                return this.driver.connect(this.url, this.props);
            }
            catch (SQLException e) {
                this.logError();
                throw e;
            }
            catch (Exception e2) {
                this.logError();
                throw new SQLException(e2);
            }
        }
        
        private void logError() {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.finer(MessageFormat.format("An error occurred getting a database connection. Driver:{0} url:{1}", this.driver, this.url));
            }
        }
        
        public String getUrl() {
            return this.url;
        }
        
        public DatabaseVendor getDatabaseVendor() {
            return this.databaseVendor;
        }
        
        void setDatabaseVendor(final DatabaseVendor databaseVendor) {
            this.databaseVendor = databaseVendor;
        }
        
        static {
            EMPTY_PROPERTIES = new Properties();
        }
    }
}
