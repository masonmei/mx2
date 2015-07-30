// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.database;

import com.newrelic.agent.bridge.datastore.DatastoreVendor;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.database.DatabaseVendor;
import java.sql.Connection;

public class DatabaseUtils
{
    public static Connection getInnerConnection(final Connection conn) {
        if (conn instanceof DelegatingConnection) {
            final DelegatingConnection delegatingConnection = (DelegatingConnection)conn;
            Connection connection = delegatingConnection.getInnermostDelegate();
            if (connection != null) {
                return getInnerConnection(connection);
            }
            if (conn instanceof DelegatingConnection.PoolGuardConnectionWrapper) {
                connection = ((DelegatingConnection.PoolGuardConnectionWrapper)conn)._nr_getDelegate();
                if (connection != null) {
                    return getInnerConnection(connection);
                }
            }
        }
        return conn;
    }
    
    public static DatabaseVendor getDatabaseVendor(final Connection connection) {
        try {
            final DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null) {
                return DatabaseVendor.UNKNOWN;
            }
            final String url = metaData.getURL();
            if (url == null) {
                return DatabaseVendor.UNKNOWN;
            }
            return DatabaseVendor.getDatabaseVendor(url);
        }
        catch (SQLException e) {
            Agent.LOG.log(Level.FINER, "Unable to determine database vendor", e);
            return DatabaseVendor.UNKNOWN;
        }
    }
    
    public static DatastoreVendor getDatastoreVendor(final DatabaseVendor databaseVendor) {
        switch (databaseVendor) {
            case MYSQL: {
                return DatastoreVendor.MySQL;
            }
            case ORACLE: {
                return DatastoreVendor.Oracle;
            }
            case MICROSOFT: {
                return DatastoreVendor.MSSQL;
            }
            case POSTGRES: {
                return DatastoreVendor.Postgres;
            }
            case DB2: {
                return DatastoreVendor.IBMDB2;
            }
            case DERBY: {
                return DatastoreVendor.Derby;
            }
            default: {
                Agent.LOG.log(Level.FINEST, "ERROR: Unknown Database Vendor: {0}. Defaulting to JDBC.", new Object[] { databaseVendor });
                return DatastoreVendor.JDBC;
            }
            case UNKNOWN: {
                return DatastoreVendor.JDBC;
            }
        }
    }
}
