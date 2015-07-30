// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.db;

import javax.naming.Context;
import javax.naming.InitialContext;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.util.PropertySetter;
import javax.sql.DataSource;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public class BindDataSourceToJNDIAction extends Action
{
    static final String DATA_SOURCE_CLASS = "dataSourceClass";
    static final String URL = "url";
    static final String USER = "user";
    static final String PASSWORD = "password";
    
    public void begin(final InterpretationContext ec, final String localName, final Attributes attributes) {
        final String dsClassName = ec.getProperty("dataSourceClass");
        if (OptionHelper.isEmpty(dsClassName)) {
            this.addWarn("dsClassName is a required parameter");
            ec.addError("dsClassName is a required parameter");
            return;
        }
        final String urlStr = ec.getProperty("url");
        final String userStr = ec.getProperty("user");
        final String passwordStr = ec.getProperty("password");
        try {
            final DataSource ds = (DataSource)OptionHelper.instantiateByClassName(dsClassName, DataSource.class, this.context);
            final PropertySetter setter = new PropertySetter(ds);
            setter.setContext(this.context);
            if (!OptionHelper.isEmpty(urlStr)) {
                setter.setProperty("url", urlStr);
            }
            if (!OptionHelper.isEmpty(userStr)) {
                setter.setProperty("user", userStr);
            }
            if (!OptionHelper.isEmpty(passwordStr)) {
                setter.setProperty("password", passwordStr);
            }
            final Context ctx = new InitialContext();
            ctx.rebind("dataSource", ds);
        }
        catch (Exception oops) {
            this.addError("Could not bind  datasource. Reported error follows.", oops);
            ec.addError("Could not not bind  datasource of type [" + dsClassName + "].");
        }
    }
    
    public void end(final InterpretationContext ec, final String name) {
    }
}
