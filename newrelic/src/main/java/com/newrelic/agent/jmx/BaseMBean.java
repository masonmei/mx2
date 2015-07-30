// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx;

import javax.management.MBeanParameterInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import java.util.MissingResourceException;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import javax.management.NotCompliantMBeanException;
import java.util.ResourceBundle;
import javax.management.StandardMBean;

abstract class BaseMBean extends StandardMBean
{
    private final ResourceBundle resourceBundle;
    
    protected BaseMBean(final Class<?> mbeanInterface) throws NotCompliantMBeanException {
        super(mbeanInterface);
        this.resourceBundle = ResourceBundle.getBundle(this.getClass().getName());
    }
    
    protected String getResourceString(final String key) {
        try {
            return this.resourceBundle.getString(key);
        }
        catch (MissingResourceException ex) {
            final String msg = MessageFormat.format("Resource file {0} error: {1}", this.getClass().getName() + ".properties", ex.toString());
            Agent.LOG.finest(msg);
            throw ex;
        }
    }
    
    protected String getDescription(final MBeanInfo info) {
        return this.getResourceString("description");
    }
    
    protected String getDescription(final MBeanAttributeInfo info) {
        try {
            return this.getResourceString("attribute." + info.getName() + ".description");
        }
        catch (Exception ex) {
            return super.getDescription(info);
        }
    }
    
    protected String getDescription(final MBeanFeatureInfo info) {
        try {
            return this.getResourceString("feature." + info.getName() + ".description");
        }
        catch (Exception ex) {
            return super.getDescription(info);
        }
    }
    
    protected String getDescription(final MBeanOperationInfo op, final MBeanParameterInfo param, final int sequence) {
        try {
            return this.getResourceString("operation." + op.getName() + '.' + param.getName() + '.' + sequence + ".description");
        }
        catch (Exception ex) {
            return super.getDescription(op, param, sequence);
        }
    }
    
    protected String getDescription(final MBeanOperationInfo info) {
        try {
            return this.getResourceString("operation." + info.getName() + ".description");
        }
        catch (Exception ex) {
            return super.getDescription(info);
        }
    }
}
