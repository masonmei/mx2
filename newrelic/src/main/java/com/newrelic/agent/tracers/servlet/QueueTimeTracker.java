// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.servlet;

import com.newrelic.agent.metric.MetricName;
import com.newrelic.agent.stats.TransactionStats;
import java.util.regex.Matcher;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.api.agent.Request;
import java.util.regex.Pattern;

public class QueueTimeTracker
{
    protected static final String REQUEST_X_QUEUE_START_HEADER = "X-Queue-Start";
    private static final Pattern REQUEST_X_QUEUE_HEADER_PATTERN;
    private final long queueTime;
    
    private QueueTimeTracker(final Request httpRequest, final long txStartTimeInNanos) {
        final String requestXQueueStartHeader = ExternalTimeTracker.getRequestHeader(httpRequest, "X-Queue-Start");
        this.queueTime = this.initQueueTime(requestXQueueStartHeader, txStartTimeInNanos);
    }
    
    private long initQueueTime(final String requestXQueueStartHeader, final long txStartTimeInNanos) {
        final long queueStartTimeInNanos = this.getQueueStartTimeFromHeader(requestXQueueStartHeader);
        if (queueStartTimeInNanos > 0L) {
            final long queueTime = txStartTimeInNanos - queueStartTimeInNanos;
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                final String msg = MessageFormat.format("Transaction start time (nanoseconds): {0}, queue start time (nanoseconds): {1}, queue time (nanoseconds): {2}", txStartTimeInNanos, queueStartTimeInNanos, queueTime);
                Agent.LOG.finest(msg);
            }
            return Math.max(0L, queueTime);
        }
        return 0L;
    }
    
    private long getQueueStartTimeFromHeader(final String requestXQueueStartHeader) {
        if (requestXQueueStartHeader != null) {
            final Matcher matcher = QueueTimeTracker.REQUEST_X_QUEUE_HEADER_PATTERN.matcher(requestXQueueStartHeader);
            if (matcher.find()) {
                final String queueStartTime = matcher.group(1);
                try {
                    return ExternalTimeTracker.parseTimestampToNano(queueStartTime);
                }
                catch (NumberFormatException e) {
                    final String msg = MessageFormat.format("Error parsing queue start time {0} in {1} header: {2}", queueStartTime, "X-Queue-Start", e);
                    Agent.LOG.log(Level.FINER, msg);
                    return 0L;
                }
            }
            final String msg2 = MessageFormat.format("Failed to parse queue start time in {0} header: {1}", "X-Queue-Start", requestXQueueStartHeader);
            Agent.LOG.log(Level.WARNING, msg2);
        }
        return 0L;
    }
    
    public long getQueueTime() {
        return this.queueTime;
    }
    
    public void recordMetrics(final TransactionStats statsEngine) {
        if (this.queueTime > 0L) {
            final MetricName name = MetricName.QUEUE_TIME;
            statsEngine.getUnscopedStats().getResponseTimeStats(name.getName()).recordResponseTimeInNanos(this.queueTime);
        }
    }
    
    public static QueueTimeTracker create(final Request httpRequest, final long txStartTimeInNanos) {
        return new QueueTimeTracker(httpRequest, txStartTimeInNanos);
    }
    
    static {
        REQUEST_X_QUEUE_HEADER_PATTERN = Pattern.compile("\\s*(?:t=)?(-?[0-9.]+)");
    }
}
