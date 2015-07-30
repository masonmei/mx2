// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.jmx;

import javax.management.MBeanRegistrationException;
import javax.management.InstanceNotFoundException;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusUtil;
import javax.management.ObjectName;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;

public class MBeanUtil
{
    static final String DOMAIN = "com.newrelic.agent.deps.ch.qos.logback.classic";
    
    public static String getObjectNameFor(final String contextName, final Class type) {
        return "ch.qos.logback.classic:Name=" + contextName + ",Type=" + type.getName();
    }
    
    public static ObjectName string2ObjectName(final Context context, final Object caller, final String objectNameAsStr) {
        final String msg = "Failed to convert [" + objectNameAsStr + "] to ObjectName";
        try {
            return new ObjectName(objectNameAsStr);
        }
        catch (MalformedObjectNameException e) {
            StatusUtil.addError(context, caller, msg, e);
            return null;
        }
        catch (NullPointerException e2) {
            StatusUtil.addError(context, caller, msg, e2);
            return null;
        }
    }
    
    public static boolean isRegistered(final MBeanServer mbs, final ObjectName objectName) {
        return mbs.isRegistered(objectName);
    }
    
    public static void createAndRegisterJMXConfigurator(final MBeanServer mbs, final LoggerContext loggerContext, final JMXConfigurator jmxConfigurator, final ObjectName objectName, final Object caller) {
        try {
            mbs.registerMBean(jmxConfigurator, objectName);
        }
        catch (Exception e) {
            StatusUtil.addError(loggerContext, caller, "Failed to create mbean", e);
        }
    }
    
    public static void unregister(final LoggerContext loggerContext, final MBeanServer mbs, final ObjectName objectName, final Object caller) {
        if (mbs.isRegistered(objectName)) {
            try {
                StatusUtil.addInfo(loggerContext, caller, "Unregistering mbean [" + objectName + "]");
                mbs.unregisterMBean(objectName);
            }
            catch (InstanceNotFoundException e) {
                StatusUtil.addError(loggerContext, caller, "Failed to unregister mbean" + objectName, e);
            }
            catch (MBeanRegistrationException e2) {
                StatusUtil.addError(loggerContext, caller, "Failed to unregister mbean" + objectName, e2);
            }
        }
        else {
            StatusUtil.addInfo(loggerContext, caller, "mbean [" + objectName + "] does not seem to be registered");
        }
    }
}
