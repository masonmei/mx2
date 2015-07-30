// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.db;

import java.sql.Connection;
import com.newrelic.agent.deps.ch.qos.logback.core.db.dialect.SQLDialectCode;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DataSourceConnectionSource extends ConnectionSourceBase
{
    private DataSource dataSource;
    
    public void start() {
        if (this.dataSource == null) {
            this.addWarn("WARNING: No data source specified");
        }
        else {
            Connection connection = null;
            try {
                connection = this.getConnection();
            }
            catch (SQLException se) {
                this.addWarn("Could not get a connection to discover the dialect to use.", se);
            }
            if (connection != null) {
                this.discoverConnectionProperties();
            }
            if (!this.supportsGetGeneratedKeys() && this.getSQLDialectCode() == SQLDialectCode.UNKNOWN_DIALECT) {
                this.addWarn("Connection does not support GetGeneratedKey method and could not discover the dialect.");
            }
        }
        super.start();
    }
    
    public Connection getConnection() throws SQLException {
        if (this.dataSource == null) {
            this.addError("WARNING: No data source specified");
            return null;
        }
        if (this.getUser() == null) {
            return this.dataSource.getConnection();
        }
        return this.dataSource.getConnection(this.getUser(), this.getPassword());
    }
    
    public DataSource getDataSource() {
        return this.dataSource;
    }
    
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
