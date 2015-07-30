// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class ContextUtil extends ContextAwareBase
{
    public ContextUtil(final Context context) {
        this.setContext(context);
    }
    
    static String getLocalHostName() throws UnknownHostException {
        final InetAddress localhost = InetAddress.getLocalHost();
        return localhost.getHostName();
    }
    
    public void addHostNameAsProperty() {
        try {
            final String localhostName = getLocalHostName();
            this.context.putProperty("HOSTNAME", localhostName);
        }
        catch (UnknownHostException e) {
            this.addError("Failed to get local hostname", e);
        }
        catch (SecurityException e2) {
            this.addError("Failed to get local hostname", e2);
        }
    }
    
    public void addProperties(final Properties props) {
        if (props == null) {
            return;
        }
        for (final String key : ((Hashtable<Object, V>)props).keySet()) {
            this.context.putProperty(key, props.getProperty(key));
        }
    }
}
