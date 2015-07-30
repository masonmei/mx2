////
//// Decompiled by Procyon v0.5.29
////
//
//package com.newrelic.agent.jmx;
//
//import java.text.MessageFormat;
//import java.util.logging.Level;
//import com.newrelic.agent.Agent;
//import java.util.concurrent.TimeUnit;
//import com.ibm.websphere.management.statistics.TimeStatistic;
//import com.ibm.websphere.management.statistics.BoundaryStatistic;
//import com.ibm.websphere.management.statistics.RangeStatistic;
//import com.ibm.websphere.management.statistics.CountStatistic;
//import com.ibm.websphere.management.statistics.JCAConnectionStats;
//import com.ibm.websphere.management.statistics.JCAConnectionPoolStats;
//import com.ibm.websphere.management.statistics.JDBCConnectionStats;
//import com.ibm.websphere.management.statistics.JDBCConnectionPoolStats;
//import com.ibm.websphere.management.statistics.JMSSessionStats;
//import com.ibm.websphere.management.statistics.JMSConnectionStats;
//import com.ibm.websphere.management.statistics.Statistic;
//import com.ibm.websphere.management.statistics.JMSStats;
//import com.ibm.websphere.management.statistics.JCAStats;
//import com.ibm.websphere.management.statistics.JDBCStats;
//import com.ibm.websphere.management.statistics.Stats;
//import java.util.Map;
//import javax.management.Attribute;
//import javax.management.ObjectInstance;
//import com.newrelic.agent.stats.StatsEngine;
//
//public class WebSphereStatsAttributeProcessor extends AbstractStatsAttributeProcessor
//{
//    public boolean process(final StatsEngine statsEngine, final ObjectInstance instance, final Attribute attribute, final String metricName, final Map<String, Float> values) {
//        final Object value = attribute.getValue();
//        if (value instanceof Stats) {
//            final boolean isBuiltInMetric = AbstractStatsAttributeProcessor.isBuiltInMetric(metricName);
//            if (value instanceof JDBCStats) {
//                pullJDBCStats(statsEngine, (JDBCStats)value, attribute, metricName, values, isBuiltInMetric);
//            }
//            else if (value instanceof JCAStats) {
//                pullJCAStats(statsEngine, (JCAStats)value, attribute, metricName, values, isBuiltInMetric);
//            }
//            else if (value instanceof JMSStats) {
//                pullJMSStats(statsEngine, (JMSStats)value, attribute, metricName, values, isBuiltInMetric);
//            }
//            else {
//                final Stats jmxStats = (Stats)value;
//                for (final Statistic statistic : jmxStats.getStatistics()) {
//                    if (isBuiltInMetric) {
//                        addJmxValue(attribute, statistic, values);
//                    }
//                    else {
//                        processStatistic(statsEngine, metricName, statistic);
//                    }
//                }
//            }
//            return true;
//        }
//        return false;
//    }
//
//    private static void pullJMSStats(final StatsEngine statsEngine, final JMSStats jmsStats, final Attribute attribute, final String metricName, final Map<String, Float> values, final boolean isBuiltInMetric) {
//        for (final JMSConnectionStats connStats : jmsStats.getConnections()) {
//            for (final JMSSessionStats current : connStats.getSessions()) {
//                grabBaseStats(statsEngine, (Stats)current, attribute, metricName, values, isBuiltInMetric);
//            }
//        }
//    }
//
//    private static void pullJDBCStats(final StatsEngine statsEngine, final JDBCStats jdbcStats, final Attribute attribute, final String metricName, final Map<String, Float> values, final boolean isBuiltInMetric) {
//        if (jdbcStats.getConnectionPools() != null) {
//            for (final JDBCConnectionPoolStats current : jdbcStats.getConnectionPools()) {
//                grabBaseStats(statsEngine, (Stats)current, attribute, metricName, values, isBuiltInMetric);
//            }
//        }
//        if (jdbcStats.getConnections() != null) {
//            for (final JDBCConnectionStats current2 : jdbcStats.getConnections()) {
//                grabBaseStats(statsEngine, (Stats)current2, attribute, metricName, values, isBuiltInMetric);
//            }
//        }
//    }
//
//    private static void pullJCAStats(final StatsEngine statsEngine, final JCAStats jcaStats, final Attribute attribute, final String metricName, final Map<String, Float> values, final boolean isBuiltInMetric) {
//        if (jcaStats.getConnectionPools() != null) {
//            for (final JCAConnectionPoolStats current : jcaStats.getConnectionPools()) {
//                grabBaseStats(statsEngine, (Stats)current, attribute, metricName, values, isBuiltInMetric);
//            }
//        }
//        if (jcaStats.getConnections() != null) {
//            for (final JCAConnectionStats current2 : jcaStats.getConnections()) {
//                grabBaseStats(statsEngine, (Stats)current2, attribute, metricName, values, isBuiltInMetric);
//            }
//        }
//    }
//
//    private static void grabBaseStats(final StatsEngine statsEngine, final Stats jmxStats, final Attribute attribute, final String metricName, final Map<String, Float> values, final boolean isBuiltInMetric) {
//        for (final Statistic statistic : jmxStats.getStatistics()) {
//            if (isBuiltInMetric) {
//                if (addJmxValue(attribute, statistic, values)) {
//                    break;
//                }
//            }
//            else {
//                processStatistic(statsEngine, metricName, statistic);
//            }
//        }
//    }
//
//    static void processStatistic(final StatsEngine statsEngine, final String metricName, final Statistic statistic) {
//        final String fullMetricName = metricName + '/' + statistic.getName();
//        if (statistic instanceof CountStatistic) {
//            final CountStatistic stat = (CountStatistic)statistic;
//            statsEngine.getStats(fullMetricName).recordDataPoint(stat.getCount());
//        }
//        else if (statistic instanceof RangeStatistic) {
//            final RangeStatistic stat2 = (RangeStatistic)statistic;
//            statsEngine.getStats(fullMetricName).recordDataPoint(stat2.getCurrent());
//        }
//        else if (statistic instanceof BoundaryStatistic) {
//            final BoundaryStatistic stat3 = (BoundaryStatistic)statistic;
//            statsEngine.getStats(fullMetricName).recordDataPoint(stat3.getLowerBound());
//            statsEngine.getStats(fullMetricName).recordDataPoint(stat3.getUpperBound());
//        }
//        else if (statistic instanceof TimeStatistic) {
//            final TimeStatistic stat4 = (TimeStatistic)statistic;
//            final TimeUnit unit = AbstractStatsAttributeProcessor.getTimeUnit(stat4.getUnit());
//            statsEngine.getResponseTimeStats(fullMetricName).recordResponseTime((int)stat4.getCount(), stat4.getTotalTime(), stat4.getMinTime(), stat4.getMaxTime(), unit);
//        }
//    }
//
//    static boolean addJmxValue(final Attribute attribute, final Statistic statistic, final Map<String, Float> values) {
//        if (attribute.getName().contains(statistic.getName())) {
//            if (statistic instanceof CountStatistic) {
//                final CountStatistic stat = (CountStatistic)statistic;
//                values.put(attribute.getName(), (float)stat.getCount());
//                return true;
//            }
//            if (statistic instanceof RangeStatistic) {
//                final RangeStatistic stat2 = (RangeStatistic)statistic;
//                values.put(attribute.getName(), (float)stat2.getCurrent());
//                return true;
//            }
//            if (statistic instanceof BoundaryStatistic) {
//                final BoundaryStatistic stat3 = (BoundaryStatistic)statistic;
//                values.put(attribute.getName(), (float)((stat3.getLowerBound() + stat3.getUpperBound()) / 2L));
//                return true;
//            }
//            if (statistic instanceof TimeStatistic) {
//                final TimeStatistic stat4 = (TimeStatistic)statistic;
//                values.put(attribute.getName(), (float)(stat4.getTotalTime() / stat4.getCount()));
//                return true;
//            }
//        }
//        else {
//            Agent.LOG.log(Level.FINEST, MessageFormat.format("Not recording stat {0} because it does not match the attribute name {1}.", statistic.getName(), attribute.getName()));
//        }
//        return false;
//    }
//}
