// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import java.util.logging.Logger;
import java.io.PrintWriter;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

class ReflectiveDataSource implements DataSource
{
    private static final String DATASOURCE_CLASS_NAME;
    private final Object dataSource;
    private final Class<?> dataSourceClass;
    
    public ReflectiveDataSource(final Object dataSource) throws ClassNotFoundException {
        (this.dataSource = dataSource).getClass();
        this.dataSourceClass = Class.forName(ReflectiveDataSource.DATASOURCE_CLASS_NAME);
    }
    
    public Connection getConnection() throws SQLException {
        return this.invoke("getConnection", new Object[0]);
    }
    
    private <T> T invoke(final String methodName, final Object... args) {
        final Class[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; ++i) {
            argTypes[i] = args[i].getClass();
        }
        try {
            return (T)this.dataSourceClass.getMethod(methodName, (Class<?>[])argTypes).invoke(this.dataSource, args);
        }
        catch (Throwable e) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, "Unable to invoke DataSource method " + methodName, e);
            }
            return null;
        }
    }
    
    public Connection getConnection(final String username, final String password) throws SQLException {
        return this.invoke("getConnection", username, password);
    }
    
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }
    
    public int getLoginTimeout() throws SQLException {
        return 0;
    }
    
    public void setLogWriter(final PrintWriter arg0) throws SQLException {
    }
    
    public void setLoginTimeout(final int arg0) throws SQLException {
    }
    
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false;
    }
    
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return null;
    }
    
    public Logger getParentLogger() {
        return null;
    }
    
    static {
        DATASOURCE_CLASS_NAME = DataSource.class.getName();
    }
}
