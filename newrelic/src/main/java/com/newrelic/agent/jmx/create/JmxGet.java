// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.create;

import java.util.regex.Matcher;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.newrelic.agent.stats.StatsEngine;
import java.util.Collection;
import java.util.Arrays;
import javax.management.MalformedObjectNameException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import com.newrelic.agent.jmx.JmxType;
import java.util.Map;
import com.newrelic.agent.extension.Extension;
import com.newrelic.agent.jmx.metrics.JmxMetric;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class JmxGet extends JmxObject
{
    private static final Pattern TYPE_QUERY_PATTERN;
    private static final Pattern PULL_VALUE_PATTERN;
    private static final Pattern PULL_ATTRIBUTE_PATTERN;
    private final String rootMetricName;
    private final boolean isPattern;
    private final Set<String> attributes;
    private final List<JmxMetric> metrics;
    private final Extension origin;
    private final JmxAttributeFilter attributeFilter;
    private final JmxMetricModifier modifier;
    
    public JmxGet(final String pObjectName, final String rootMetricName, final String safeName, final Map<JmxType, List<String>> pAttributesToType, final Extension origin) throws MalformedObjectNameException {
        super(pObjectName, safeName);
        this.origin = origin;
        this.attributeFilter = null;
        this.modifier = null;
        this.rootMetricName = this.getRootMetricName(rootMetricName);
        this.isPattern = isPattern(rootMetricName);
        this.attributes = new HashSet<String>();
        this.metrics = new ArrayList<JmxMetric>();
        for (final Map.Entry<JmxType, List<String>> current : pAttributesToType.entrySet()) {
            final JmxType type = current.getKey();
            final List<String> attrs = current.getValue();
            for (final String att : attrs) {
                this.attributes.add(att);
                this.metrics.add(JmxMetric.create(att, type));
            }
        }
    }
    
    public JmxGet(final String pObjectName, final String safeName, final String pRootMetric, final List<JmxMetric> pMetrics, final JmxAttributeFilter attributeFilter, final JmxMetricModifier pModifier) throws MalformedObjectNameException {
        super(pObjectName, safeName);
        this.origin = null;
        this.attributeFilter = attributeFilter;
        this.modifier = pModifier;
        this.rootMetricName = this.getRootMetricName(pRootMetric);
        this.isPattern = isPattern(this.rootMetricName);
        if (pMetrics == null) {
            this.metrics = new ArrayList<JmxMetric>();
        }
        else {
            this.metrics = pMetrics;
        }
        this.attributes = new HashSet<String>();
        for (final JmxMetric m : this.metrics) {
            this.attributes.addAll(Arrays.asList(m.getAttributes()));
        }
    }
    
    public abstract void recordStats(final StatsEngine p0, final Map<ObjectName, Map<String, Float>> p1, final MBeanServer p2);
    
    private static boolean isPattern(final String rootMetricName) {
        return rootMetricName != null && rootMetricName.contains("{");
    }
    
    private String getRootMetricName(String root) {
        if (root != null) {
            if (!root.endsWith("/")) {
                root += "/";
            }
            if (!root.startsWith("JMX/") && !root.startsWith("JmxBuiltIn")) {
                root = "JMX/" + root;
            }
        }
        return root;
    }
    
    public Collection<String> getAttributes() {
        return this.attributes;
    }
    
    public String getRootMetricName(final ObjectName actualName, final MBeanServer server) {
        if (this.rootMetricName != null) {
            return this.pullAttValuesFromName(actualName, server);
        }
        return this.getDefaultName(actualName);
    }
    
    private String pullAttValuesFromName(final ObjectName actualName, final MBeanServer server) {
        if (!this.isPattern) {
            return this.rootMetricName;
        }
        final StringBuffer sb = new StringBuffer();
        final Matcher m = JmxGet.PULL_VALUE_PATTERN.matcher(this.rootMetricName);
        final Map<String, String> keyProperties = actualName.getKeyPropertyList();
        String value = null;
        while (m.find()) {
            String key = m.group(1);
            final Matcher attributeMatcher = JmxGet.PULL_ATTRIBUTE_PATTERN.matcher(key);
            if (attributeMatcher.matches()) {
                key = attributeMatcher.group(1);
                try {
                    value = server.getAttribute(actualName, key).toString();
                }
                catch (Throwable e) {
                    Agent.LOG.log(Level.FINEST, e, e.getMessage(), new Object[0]);
                }
            }
            else {
                value = keyProperties.get(key);
            }
            if (value != null) {
                m.appendReplacement(sb, cleanValue(value));
            }
            else {
                m.appendReplacement(sb, "");
            }
        }
        m.appendTail(sb);
        if (sb.charAt(sb.length() - 1) != '/') {
            sb.append('/');
        }
        if (this.modifier == null) {
            return sb.toString();
        }
        return this.modifier.getMetricName(sb.toString());
    }
    
    protected static String cleanValue(String value) {
        value = value.trim();
        if (value.length() > 0 && value.charAt(0) == '/') {
            return value.substring(1);
        }
        return value;
    }
    
    private String getDefaultName(final ObjectName actualName) {
        final Map<String, String> keyProperties = actualName.getKeyPropertyList();
        final String type = keyProperties.remove("type");
        final StringBuilder rootPath = new StringBuilder("JMX").append('/');
        if (actualName.getDomain() != null) {
            rootPath.append(actualName.getDomain()).append('/');
        }
        rootPath.append(type);
        if (keyProperties.size() > 1) {
            final String str = this.getObjectNameString();
            final Matcher matcher = JmxGet.TYPE_QUERY_PATTERN.matcher(str);
            while (matcher.find()) {
                final String group = matcher.group(1);
                final String val = keyProperties.remove(group);
                if (val != null) {
                    rootPath.append('/');
                    rootPath.append(formatSegment(val));
                }
            }
        }
        if (keyProperties.size() == 1) {
            rootPath.append('/');
            rootPath.append(formatSegment(keyProperties.entrySet().iterator().next().getValue()));
        }
        rootPath.append('/');
        return rootPath.toString();
    }
    
    private static String formatSegment(final String metricSegment) {
        if (metricSegment.length() > 0 && metricSegment.charAt(0) == '/') {
            return metricSegment.substring(1);
        }
        return metricSegment;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("object_name: ").append(this.getObjectNameString());
        sb.append(" attributes: [");
        final Iterator<JmxMetric> it = this.metrics.iterator();
        while (it.hasNext()) {
            final JmxMetric metric = it.next();
            sb.append(metric.getAttributeMetricName()).append(" type: ").append(metric.getType().getYmlName());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    public Extension getOrigin() {
        return this.origin;
    }
    
    protected JmxAttributeFilter getJmxAttributeFilter() {
        return this.attributeFilter;
    }
    
    protected List<JmxMetric> getJmxMetrics() {
        return this.metrics;
    }
    
    static {
        TYPE_QUERY_PATTERN = Pattern.compile(",(.*?)=");
        PULL_VALUE_PATTERN = Pattern.compile("\\{(.*?)\\}");
        PULL_ATTRIBUTE_PATTERN = Pattern.compile("\\:(.*?)\\:");
    }
}
