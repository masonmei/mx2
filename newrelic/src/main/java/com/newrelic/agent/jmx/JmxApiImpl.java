// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx;

import com.newrelic.agent.jmx.values.WebSphereJmxValues;
import com.newrelic.agent.jmx.values.WebSphere7JmxValues;
import com.newrelic.agent.jmx.values.KafkaConsumerJmxValues;
import com.newrelic.agent.jmx.values.KafkaProducerJmxValues;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.concurrent.ConcurrentMap;
import com.newrelic.agent.bridge.JmxApi;

public class JmxApiImpl implements JmxApi
{
    private ConcurrentMap<String, Boolean> addedJmx;
    
    public JmxApiImpl() {
        this.addedJmx = Maps.newConcurrentMap();
    }
    
    public void addJmxMBeanGroup(final String name) {
        if (!this.addedJmx.containsKey(name)) {
            final JmxFrameworkValues jmx = this.getJmxFrameworkValues(name);
            if (null != jmx) {
                final Boolean alreadyAdded = this.addedJmx.putIfAbsent(name, Boolean.TRUE);
                if (null == alreadyAdded || !alreadyAdded) {
                    ServiceFactory.getJmxService().addJmxFrameworkValues(jmx);
                    Agent.LOG.log(Level.FINE, "Added JMX for {0}", new Object[] { jmx.getPrefix() });
                }
                else {
                    Agent.LOG.log(Level.FINE, "Skipped JMX. Already added jmx framework: {0}", new Object[] { name });
                }
            }
            else {
                Agent.LOG.log(Level.FINE, "Skipped JMX. Unknown jmx framework: {0}", new Object[] { name });
            }
        }
    }
    
    private JmxFrameworkValues getJmxFrameworkValues(final String prefixName) {
        if (prefixName != null) {
            if (prefixName.equals(KafkaProducerJmxValues.PREFIX)) {
                return new KafkaProducerJmxValues();
            }
            if (prefixName.equals(KafkaConsumerJmxValues.PREFIX)) {
                return new KafkaConsumerJmxValues();
            }
            if (prefixName.equals(WebSphere7JmxValues.PREFIX)) {
                return new WebSphere7JmxValues();
            }
            if (prefixName.equals(WebSphereJmxValues.PREFIX)) {
                return new WebSphereJmxValues();
            }
        }
        return null;
    }
}
