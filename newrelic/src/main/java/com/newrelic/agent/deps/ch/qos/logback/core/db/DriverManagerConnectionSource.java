// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.db;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;

public class DriverManagerConnectionSource extends ConnectionSourceBase
{
    private String driverClass;
    private String url;
    
    public DriverManagerConnectionSource() {
        this.driverClass = null;
        this.url = null;
    }
    
    public void start() {
        try {
            if (this.driverClass != null) {
                Class.forName(this.driverClass);
                this.discoverConnectionProperties();
            }
            else {
                this.addError("WARNING: No JDBC driver specified for logback DriverManagerConnectionSource.");
            }
        }
        catch (ClassNotFoundException cnfe) {
            this.addError("Could not load JDBC driver class: " + this.driverClass, cnfe);
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (this.getUser() == null) {
            return DriverManager.getConnection(this.url);
        }
        return DriverManager.getConnection(this.url, this.getUser(), this.getPassword());
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
    
    public String getDriverClass() {
        return this.driverClass;
    }
    
    public void setDriverClass(final String driverClass) {
        this.driverClass = driverClass;
    }
}
