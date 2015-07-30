// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.servlet;

import com.newrelic.agent.metric.MetricName;
import java.util.Iterator;
import com.newrelic.agent.stats.TransactionStats;
import java.util.regex.Matcher;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.HashMap;
import java.util.Collections;
import com.newrelic.api.agent.Request;
import java.util.Map;
import java.util.regex.Pattern;

public class ServerTimeTracker
{
    protected static final String MISSING_SERVER_NAME_PREFIX = "unknown";
    public static final String REQUEST_X_START_HEADER = "X-Request-Start";
    private static final Pattern REQUEST_X_START_HEADER_PATTERN;
    private final long totalServerTime;
    private final Map<String, Long> serverTimes;
    
    private ServerTimeTracker(final Request httpRequest, final long txStartTimeInNanos, final long queueTime) {
        final String requestXStartHeader = ExternalTimeTracker.getRequestHeader(httpRequest, "X-Request-Start");
        if (requestXStartHeader == null) {
            this.serverTimes = Collections.emptyMap();
            this.totalServerTime = 0L;
        }
        else {
            this.serverTimes = new HashMap<String, Long>();
            this.totalServerTime = this.initRequestTime(requestXStartHeader, txStartTimeInNanos, queueTime);
        }
    }
    
    private long initRequestTime(final String requestXStartHeader, final long txStartTimeInNanos, final long queueTime) {
        long totalServerTime = 0L;
        int index = 0;
        long lastServerStartTimeInNanos = 0L;
        String lastServerName = null;
        final Matcher matcher = ServerTimeTracker.REQUEST_X_START_HEADER_PATTERN.matcher(requestXStartHeader);
        while (matcher.find()) {
            ++index;
            String serverName = matcher.group(1);
            if (serverName == null || serverName.length() == 0) {
                serverName = "unknown" + String.valueOf(index);
            }
            long serverStartTimeInNanos = 0L;
            final String serverStartTime = (matcher.group(2) != null) ? matcher.group(2) : matcher.group(3);
            try {
                serverStartTimeInNanos = ExternalTimeTracker.parseTimestampToNano(serverStartTime);
                if (lastServerName != null) {
                    final long serverTimeInNanos = Math.max(0L, serverStartTimeInNanos - lastServerStartTimeInNanos);
                    if (Agent.LOG.isLoggable(Level.FINEST)) {
                        final String msg = MessageFormat.format("{0} start time (nanoseconds): {1}, {2} start time (in nanoseconds): {3}, {4} time (in nanoseconds): {5}", serverName, serverStartTimeInNanos, lastServerName, lastServerStartTimeInNanos, lastServerName, serverTimeInNanos);
                        Agent.LOG.finest(msg);
                    }
                    if (serverTimeInNanos > 0L) {
                        this.serverTimes.put(lastServerName, serverTimeInNanos);
                        totalServerTime += serverTimeInNanos;
                    }
                }
                lastServerStartTimeInNanos = serverStartTimeInNanos;
                lastServerName = serverName;
            }
            catch (NumberFormatException e) {
                final String msg2 = MessageFormat.format("Error parsing server time {0} in {1}: {2}", serverStartTime, "X-Request-Start", e);
                Agent.LOG.log(Level.FINER, msg2);
            }
        }
        if (lastServerName != null) {
            final long serverTimeInNanos2 = Math.max(0L, txStartTimeInNanos - lastServerStartTimeInNanos - queueTime);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                final String msg3 = MessageFormat.format("Transaction start time (nanoseconds): {0}, {1} start time (in nanoseconds): {2}, queue time (in nanoseconds): {3}, {4} time (in nanoseconds): {5}", txStartTimeInNanos, lastServerName, lastServerStartTimeInNanos, queueTime, lastServerName, serverTimeInNanos2);
                Agent.LOG.finest(msg3);
            }
            if (serverTimeInNanos2 > 0L) {
                this.serverTimes.put(lastServerName, serverTimeInNanos2);
                totalServerTime += serverTimeInNanos2;
            }
        }
        return totalServerTime;
    }
    
    public long getServerTime() {
        return this.totalServerTime;
    }
    
    public void recordMetrics(final TransactionStats statsEngine) {
        if (this.totalServerTime > 0L) {
            this.recordAllServerTimeMetric(statsEngine, this.totalServerTime);
            for (final Map.Entry<String, Long> entry : this.serverTimes.entrySet()) {
                this.recordServerTimeMetric(statsEngine, entry.getKey(), entry.getValue());
            }
        }
    }
    
    private void recordServerTimeMetric(final TransactionStats statsEngine, final String serverName, final long serverTime) {
        final String name = "WebFrontend/WebServer/" + serverName;
        statsEngine.getUnscopedStats().getResponseTimeStats(name).recordResponseTimeInNanos(serverTime);
        if (Agent.LOG.isLoggable(Level.FINEST)) {
            final String msg = MessageFormat.format("Recorded metric: {0}, value: {1}", name, String.valueOf(serverTime));
            Agent.LOG.finest(msg);
        }
    }
    
    private void recordAllServerTimeMetric(final TransactionStats statsEngine, final long totalServerTime) {
        final MetricName name = MetricName.QUEUE_TIME;
        statsEngine.getUnscopedStats().getResponseTimeStats(name.getName()).recordResponseTimeInNanos(totalServerTime);
    }
    
    public static ServerTimeTracker create(final Request httpRequest, final long txStartTimeInNanos, final long queueTime) {
        return new ServerTimeTracker(httpRequest, txStartTimeInNanos, queueTime);
    }
    
    static {
        REQUEST_X_START_HEADER_PATTERN = Pattern.compile("([^\\s\\/,=\\(\\)]+)? ?t=(-?[0-9.]+)|(-?[0-9.]+)");
    }
}
