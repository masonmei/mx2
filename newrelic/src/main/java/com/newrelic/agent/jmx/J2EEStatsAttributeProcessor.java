// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.management.j2ee.statistics.TimeStatistic;
import javax.management.j2ee.statistics.BoundaryStatistic;
import javax.management.j2ee.statistics.RangeStatistic;
import javax.management.j2ee.statistics.CountStatistic;
import javax.management.j2ee.statistics.Statistic;
import javax.management.j2ee.statistics.JCAConnectionStats;
import javax.management.j2ee.statistics.JCAConnectionPoolStats;
import javax.management.j2ee.statistics.JDBCConnectionStats;
import javax.management.j2ee.statistics.JDBCConnectionPoolStats;
import javax.management.j2ee.statistics.JMSSessionStats;
import javax.management.j2ee.statistics.JMSConnectionStats;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import javax.management.j2ee.statistics.JMSStats;
import javax.management.j2ee.statistics.JCAStats;
import javax.management.j2ee.statistics.JDBCStats;
import javax.management.j2ee.statistics.Stats;
import java.util.Map;
import javax.management.Attribute;
import javax.management.ObjectInstance;
import com.newrelic.agent.stats.StatsEngine;

public class J2EEStatsAttributeProcessor extends AbstractStatsAttributeProcessor
{
    public boolean process(final StatsEngine statsEngine, final ObjectInstance instance, final Attribute attribute, final String metricName, final Map<String, Float> values) {
        final Object value = attribute.getValue();
        if (value instanceof Stats) {
            final boolean isBuiltInMetric = AbstractStatsAttributeProcessor.isBuiltInMetric(metricName);
            if (value instanceof JDBCStats) {
                pullJDBCStats(statsEngine, (JDBCStats)value, attribute, metricName, values, isBuiltInMetric);
            }
            else if (value instanceof JCAStats) {
                pullJCAStats(statsEngine, (JCAStats)value, attribute, metricName, values, isBuiltInMetric);
            }
            else if (value instanceof JMSStats) {
                pullJMSStats(statsEngine, (JMSStats)value, attribute, metricName, values, isBuiltInMetric);
            }
            else {
                final Stats jmxStats = (Stats)value;
                grabBaseStats(statsEngine, jmxStats, attribute, metricName, values, isBuiltInMetric);
            }
            return true;
        }
        Agent.LOG.finer(MessageFormat.format("Attribute value is not a javax.management.j2ee.statistics.Stats: {0}", value.getClass().getName()));
        return false;
    }
    
    private static void pullJMSStats(final StatsEngine statsEngine, final JMSStats jmsStats, final Attribute attribute, final String metricName, final Map<String, Float> values, final boolean isBuiltInMetric) {
        for (final JMSConnectionStats connStats : jmsStats.getConnections()) {
            for (final JMSSessionStats current : connStats.getSessions()) {
                grabBaseStats(statsEngine, (Stats)current, attribute, metricName, values, isBuiltInMetric);
            }
        }
    }
    
    private static void pullJDBCStats(final StatsEngine statsEngine, final JDBCStats jdbcStats, final Attribute attribute, final String metricName, final Map<String, Float> values, final boolean isBuiltInMetric) {
        if (jdbcStats.getConnectionPools() != null) {
            for (final JDBCConnectionPoolStats current : jdbcStats.getConnectionPools()) {
                grabBaseStats(statsEngine, (Stats)current, attribute, metricName, values, isBuiltInMetric);
            }
        }
        if (jdbcStats.getConnections() != null) {
            for (final JDBCConnectionStats current2 : jdbcStats.getConnections()) {
                grabBaseStats(statsEngine, (Stats)current2, attribute, metricName, values, isBuiltInMetric);
            }
        }
    }
    
    private static void pullJCAStats(final StatsEngine statsEngine, final JCAStats jcaStats, final Attribute attribute, final String metricName, final Map<String, Float> values, final boolean isBuiltInMetric) {
        if (jcaStats.getConnectionPools() != null) {
            for (final JCAConnectionPoolStats current : jcaStats.getConnectionPools()) {
                grabBaseStats(statsEngine, (Stats)current, attribute, metricName, values, isBuiltInMetric);
            }
        }
        if (jcaStats.getConnections() != null) {
            for (final JCAConnectionStats current2 : jcaStats.getConnections()) {
                grabBaseStats(statsEngine, (Stats)current2, attribute, metricName, values, isBuiltInMetric);
            }
        }
    }
    
    private static void grabBaseStats(final StatsEngine statsEngine, final Stats jmxStats, final Attribute attribute, final String metricName, final Map<String, Float> values, final boolean isBuiltInMetric) {
        for (final Statistic statistic : jmxStats.getStatistics()) {
            if (isBuiltInMetric) {
                if (addJmxValue(attribute, statistic, values)) {
                    break;
                }
            }
            else {
                processStatistic(statsEngine, metricName, attribute, statistic);
            }
        }
    }
    
    static void processStatistic(final StatsEngine statsEngine, final String metricName, final Attribute attribute, final Statistic statistic) {
        final String fullMetricName = metricName + '/' + statistic.getName();
        Agent.LOG.finer(MessageFormat.format("Processing J2EE statistic: {0} class: {1}", statistic.getName(), statistic.getClass().getName()));
        if (statistic instanceof CountStatistic) {
            final CountStatistic stat = (CountStatistic)statistic;
            statsEngine.getStats(fullMetricName).recordDataPoint(stat.getCount());
        }
        else if (statistic instanceof RangeStatistic) {
            final RangeStatistic stat2 = (RangeStatistic)statistic;
            statsEngine.getStats(fullMetricName).recordDataPoint(stat2.getCurrent());
        }
        else if (statistic instanceof BoundaryStatistic) {
            final BoundaryStatistic stat3 = (BoundaryStatistic)statistic;
            statsEngine.getStats(fullMetricName).recordDataPoint(stat3.getLowerBound());
            statsEngine.getStats(fullMetricName).recordDataPoint(stat3.getUpperBound());
        }
        else if (statistic instanceof TimeStatistic) {
            final TimeStatistic stat4 = (TimeStatistic)statistic;
            final TimeUnit unit = AbstractStatsAttributeProcessor.getTimeUnit(stat4.getUnit());
            statsEngine.getResponseTimeStats(fullMetricName).recordResponseTime((int)stat4.getCount(), stat4.getTotalTime(), stat4.getMinTime(), stat4.getMaxTime(), unit);
        }
        else {
            Agent.LOG.log(Level.FINEST, "Not supported: {0}", new Object[] { statistic.getClass().getName() });
        }
        Agent.LOG.finer(MessageFormat.format("Processed J2EE statistic: {0} att: {1}", fullMetricName, statistic.getName()));
    }
    
    static boolean addJmxValue(final Attribute attribute, final Statistic statistic, final Map<String, Float> values) {
        if (attribute.getName().contains(statistic.getName())) {
            Agent.LOG.finer(MessageFormat.format("Adding J2EE statistic to List: {0} class: {1}", attribute.getName(), statistic.getClass().getName()));
            if (statistic instanceof CountStatistic) {
                final CountStatistic stat = (CountStatistic)statistic;
                values.put(attribute.getName(), (float)stat.getCount());
                return true;
            }
            if (statistic instanceof RangeStatistic) {
                final RangeStatistic stat2 = (RangeStatistic)statistic;
                values.put(attribute.getName(), (float)stat2.getCurrent());
                return true;
            }
            if (statistic instanceof BoundaryStatistic) {
                final BoundaryStatistic stat3 = (BoundaryStatistic)statistic;
                values.put(attribute.getName(), (float)((stat3.getLowerBound() + stat3.getUpperBound()) / 2L));
                return true;
            }
            if (statistic instanceof TimeStatistic) {
                final TimeStatistic stat4 = (TimeStatistic)statistic;
                if (stat4.getCount() == 0L) {
                    values.put(attribute.getName(), 0.0f);
                }
                else {
                    values.put(attribute.getName(), (float)(stat4.getTotalTime() / stat4.getCount()));
                }
                return true;
            }
            Agent.LOG.finer(MessageFormat.format("Added J2EE statistic: {0}", attribute.getName()));
        }
        else {
            Agent.LOG.log(Level.FINEST, MessageFormat.format("Ignoring stat {0}. Looking for att name {1}.", statistic.getName(), attribute.getName()));
        }
        return false;
    }
}
