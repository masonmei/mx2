// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.db;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

public class JNDIConnectionSource extends ConnectionSourceBase
{
    private String jndiLocation;
    private DataSource dataSource;
    
    public JNDIConnectionSource() {
        this.jndiLocation = null;
        this.dataSource = null;
    }
    
    public void start() {
        if (this.jndiLocation == null) {
            this.addError("No JNDI location specified for JNDIConnectionSource.");
        }
        this.discoverConnectionProperties();
    }
    
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            if (this.dataSource == null) {
                this.dataSource = this.lookupDataSource();
            }
            if (this.getUser() != null) {
                this.addWarn("Ignoring property [user] with value [" + this.getUser() + "] for obtaining a connection from a DataSource.");
            }
            conn = this.dataSource.getConnection();
        }
        catch (NamingException ne) {
            this.addError("Error while getting data source", ne);
            throw new SQLException("NamingException while looking up DataSource: " + ne.getMessage());
        }
        catch (ClassCastException cce) {
            this.addError("ClassCastException while looking up DataSource.", cce);
            throw new SQLException("ClassCastException while looking up DataSource: " + cce.getMessage());
        }
        return conn;
    }
    
    public String getJndiLocation() {
        return this.jndiLocation;
    }
    
    public void setJndiLocation(final String jndiLocation) {
        this.jndiLocation = jndiLocation;
    }
    
    private DataSource lookupDataSource() throws NamingException, SQLException {
        this.addInfo("Looking up [" + this.jndiLocation + "] in JNDI");
        final Context initialContext = new InitialContext();
        final Object obj = initialContext.lookup(this.jndiLocation);
        final DataSource ds = (DataSource)obj;
        if (ds == null) {
            throw new SQLException("Failed to obtain data source from JNDI location " + this.jndiLocation);
        }
        return ds;
    }
}
