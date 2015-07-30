// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import java.lang.management.ManagementFactory;
import com.newrelic.agent.deps.ch.qos.logback.classic.jmx.MBeanUtil;
import com.newrelic.agent.deps.ch.qos.logback.classic.jmx.JMXConfigurator;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public class JMXConfiguratorAction extends Action
{
    static final String OBJECT_NAME_ATTRIBUTE_NAME = "objectName";
    static final String CONTEXT_NAME_ATTRIBUTE_NAME = "contextName";
    static final char JMX_NAME_SEPARATOR = ',';
    
    public void begin(final InterpretationContext ec, final String name, final Attributes attributes) throws ActionException {
        this.addInfo("begin");
        String contextName = this.context.getName();
        final String contextNameAttributeVal = attributes.getValue("contextName");
        if (!OptionHelper.isEmpty(contextNameAttributeVal)) {
            contextName = contextNameAttributeVal;
        }
        final String objectNameAttributeVal = attributes.getValue("objectName");
        String objectNameAsStr;
        if (OptionHelper.isEmpty(objectNameAttributeVal)) {
            objectNameAsStr = MBeanUtil.getObjectNameFor(contextName, JMXConfigurator.class);
        }
        else {
            objectNameAsStr = objectNameAttributeVal;
        }
        final ObjectName objectName = MBeanUtil.string2ObjectName(this.context, this, objectNameAsStr);
        if (objectName == null) {
            this.addError("Failed construct ObjectName for [" + objectNameAsStr + "]");
            return;
        }
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        if (!MBeanUtil.isRegistered(mbs, objectName)) {
            final JMXConfigurator jmxConfigurator = new JMXConfigurator((LoggerContext)this.context, mbs, objectName);
            try {
                mbs.registerMBean(jmxConfigurator, objectName);
            }
            catch (Exception e) {
                this.addError("Failed to create mbean", e);
            }
        }
    }
    
    public void end(final InterpretationContext ec, final String name) throws ActionException {
    }
}
