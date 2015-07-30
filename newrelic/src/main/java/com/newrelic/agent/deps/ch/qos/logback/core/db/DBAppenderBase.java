// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.db;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.newrelic.agent.deps.ch.qos.logback.core.db.dialect.SQLDialectCode;
import com.newrelic.agent.deps.ch.qos.logback.core.db.dialect.DBUtil;
import java.lang.reflect.Method;
import com.newrelic.agent.deps.ch.qos.logback.core.db.dialect.SQLDialect;
import com.newrelic.agent.deps.ch.qos.logback.core.UnsynchronizedAppenderBase;

public abstract class DBAppenderBase<E> extends UnsynchronizedAppenderBase<E>
{
    protected ConnectionSource connectionSource;
    protected boolean cnxSupportsGetGeneratedKeys;
    protected boolean cnxSupportsBatchUpdates;
    protected SQLDialect sqlDialect;
    
    public DBAppenderBase() {
        this.cnxSupportsGetGeneratedKeys = false;
        this.cnxSupportsBatchUpdates = false;
    }
    
    protected abstract Method getGeneratedKeysMethod();
    
    protected abstract String getInsertSQL();
    
    public void start() {
        if (this.connectionSource == null) {
            throw new IllegalStateException("DBAppender cannot function without a connection source");
        }
        this.sqlDialect = DBUtil.getDialectFromCode(this.connectionSource.getSQLDialectCode());
        if (this.getGeneratedKeysMethod() != null) {
            this.cnxSupportsGetGeneratedKeys = this.connectionSource.supportsGetGeneratedKeys();
        }
        else {
            this.cnxSupportsGetGeneratedKeys = false;
        }
        this.cnxSupportsBatchUpdates = this.connectionSource.supportsBatchUpdates();
        if (!this.cnxSupportsGetGeneratedKeys && this.sqlDialect == null) {
            throw new IllegalStateException("DBAppender cannot function if the JDBC driver does not support getGeneratedKeys method *and* without a specific SQL dialect");
        }
        super.start();
    }
    
    public ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }
    
    public void setConnectionSource(final ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }
    
    public void append(final E eventObject) {
        Connection connection = null;
        try {
            connection = this.connectionSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement insertStatement;
            if (this.cnxSupportsGetGeneratedKeys) {
                String EVENT_ID_COL_NAME = "EVENT_ID";
                if (this.connectionSource.getSQLDialectCode() == SQLDialectCode.POSTGRES_DIALECT) {
                    EVENT_ID_COL_NAME = EVENT_ID_COL_NAME.toLowerCase();
                }
                insertStatement = connection.prepareStatement(this.getInsertSQL(), new String[] { EVENT_ID_COL_NAME });
            }
            else {
                insertStatement = connection.prepareStatement(this.getInsertSQL());
            }
            final long eventId;
            synchronized (this) {
                this.subAppend(eventObject, connection, insertStatement);
                eventId = this.selectEventId(insertStatement, connection);
            }
            this.secondarySubAppend(eventObject, connection, eventId);
            this.close(insertStatement);
            connection.commit();
        }
        catch (Throwable sqle) {
            this.addError("problem appending event", sqle);
        }
        finally {
            DBHelper.closeConnection(connection);
        }
    }
    
    protected abstract void subAppend(final E p0, final Connection p1, final PreparedStatement p2) throws Throwable;
    
    protected abstract void secondarySubAppend(final E p0, final Connection p1, final long p2) throws Throwable;
    
    protected long selectEventId(final PreparedStatement insertStatement, final Connection connection) throws SQLException, InvocationTargetException {
        ResultSet rs = null;
        Statement idStatement = null;
        boolean gotGeneratedKeys = false;
        if (this.cnxSupportsGetGeneratedKeys) {
            try {
                rs = (ResultSet)this.getGeneratedKeysMethod().invoke(insertStatement, (Object[])null);
                gotGeneratedKeys = true;
            }
            catch (InvocationTargetException ex) {
                final Throwable target = ex.getTargetException();
                if (target instanceof SQLException) {
                    throw (SQLException)target;
                }
                throw ex;
            }
            catch (IllegalAccessException ex2) {
                this.addWarn("IllegalAccessException invoking PreparedStatement.getGeneratedKeys", ex2);
            }
        }
        if (!gotGeneratedKeys) {
            idStatement = connection.createStatement();
            idStatement.setMaxRows(1);
            final String selectInsertIdStr = this.sqlDialect.getSelectInsertId();
            rs = idStatement.executeQuery(selectInsertIdStr);
        }
        rs.next();
        final long eventId = rs.getLong(1);
        rs.close();
        this.close(idStatement);
        return eventId;
    }
    
    void close(final Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
    }
    
    public void stop() {
        super.stop();
    }
}
