// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.create;

import java.util.ArrayList;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.List;
import com.newrelic.agent.jmx.JmxType;
import java.util.Map;

public class JmxYmlParser implements JmxConfiguration
{
    private static final String YML_PROP_OBJECT_NAME = "object_name";
    private static final String YML_PROP_ROOT_METRIC_NAME = "root_metric_name";
    private static final String YML_PROP_ENABLED = "enabled";
    private static final String YML_PROP_METRICS = "metrics";
    private static final String YML_PROP_ATTRS = "attributes";
    private static final String YML_PROP_ATT = "attribute";
    private static final String YML_PROP_TYPE = "type";
    private final Map<?, ?> jmxConfig;
    
    public JmxYmlParser(final Map<?, ?> pJmxConfig) {
        this.jmxConfig = pJmxConfig;
    }
    
    public String getObjectName() {
        return (String)this.jmxConfig.get("object_name");
    }
    
    public String getRootMetricName() {
        return (String)this.jmxConfig.get("root_metric_name");
    }
    
    public boolean getEnabled() {
        final Boolean isEnabled = (Boolean)this.jmxConfig.get("enabled");
        return isEnabled == null || isEnabled;
    }
    
    public Map<JmxType, List<String>> getAttrs() {
        final List<?> metrics = (List<?>)this.jmxConfig.get("metrics");
        if (metrics == null) {
            Agent.LOG.log(Level.WARNING, "There is no 'metric' property in the JMX configuration file. Please verify the format of your yml file.");
            return null;
        }
        final Map<JmxType, List<String>> attrs = new HashMap<JmxType, List<String>>(3);
        for (final Map<?, ?> metric : metrics) {
            final JmxType type = findType(metric);
            final List<String> attList = findAttributes(metric);
            if (attList.size() > 0) {
                final List<String> alreadyAdded = attrs.get(type);
                if (alreadyAdded == null) {
                    attrs.put(type, attList);
                }
                else {
                    alreadyAdded.addAll(attList);
                }
            }
        }
        return attrs;
    }
    
    private static JmxType findType(final Map<?, ?> metricMap) {
        final String type = (String)metricMap.get("type");
        if (type == null || type.equals(JmxType.MONOTONICALLY_INCREASING.getYmlName())) {
            return JmxType.MONOTONICALLY_INCREASING;
        }
        if (type.equals(JmxType.SIMPLE.getYmlName())) {
            return JmxType.SIMPLE;
        }
        final String msg = MessageFormat.format("Unknown JMX metric type: {0}.  Using default type: {1}", type, JmxType.MONOTONICALLY_INCREASING);
        Agent.LOG.warning(msg);
        return JmxType.MONOTONICALLY_INCREASING;
    }
    
    private static List<String> findAttributes(final Map<?, ?> metricMap) {
        final List<String> result = new ArrayList<String>();
        final String attributes = (String)metricMap.get("attributes");
        if (attributes != null) {
            for (final String attribute : attributes.split(",")) {
                final String current = attribute.trim();
                if (current.length() != 0) {
                    result.add(current);
                }
            }
        }
        else {
            final String attribute2 = (String)metricMap.get("attribute");
            if (attribute2 != null && attribute2.trim().length() > 0) {
                result.add(attribute2.trim());
            }
        }
        return result;
    }
}
