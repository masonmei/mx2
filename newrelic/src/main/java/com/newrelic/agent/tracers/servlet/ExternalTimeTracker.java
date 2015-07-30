// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.servlet;

import java.util.Arrays;
import java.util.Iterator;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.stats.TransactionStats;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.Agent;
import com.newrelic.api.agent.Request;
import java.util.List;

public class ExternalTimeTracker
{
    static final long EARLIEST_ACCEPTABLE_TIMESTAMP_NANO = 946684800000000000L;
    static final List<Integer> MAGNITUDES;
    private final QueueTimeTracker queueTimeTracker;
    private final ServerTimeTracker serverTimeTracker;
    private final long externalTime;
    
    private ExternalTimeTracker(final Request httpRequest, final long txStartTimeInMillis) {
        if (httpRequest == null) {
            Agent.LOG.finer("Unable to get headers: HttpRequest is null");
        }
        final long txStartTimeInNanos = parseTimestampToNano(txStartTimeInMillis);
        this.queueTimeTracker = QueueTimeTracker.create(httpRequest, txStartTimeInNanos);
        final long queueTime = this.queueTimeTracker.getQueueTime();
        this.serverTimeTracker = ServerTimeTracker.create(httpRequest, txStartTimeInNanos, queueTime);
        final long serverTime = this.serverTimeTracker.getServerTime();
        this.externalTime = TimeUnit.MILLISECONDS.convert(queueTime + serverTime, TimeUnit.NANOSECONDS);
    }
    
    public long getExternalTime() {
        return this.externalTime;
    }
    
    public void recordMetrics(final TransactionStats statsEngine) {
        this.queueTimeTracker.recordMetrics(statsEngine);
        this.serverTimeTracker.recordMetrics(statsEngine);
    }
    
    protected static String getRequestHeader(final Request httpRequest, final String headerName) {
        if (httpRequest == null) {
            return null;
        }
        try {
            final String header = httpRequest.getHeader(headerName);
            if (header != null && Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Got {0} header: {1}", headerName, header);
                Agent.LOG.finer(msg);
            }
            return header;
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("Error getting {0} header: {1}", headerName, t.toString());
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.FINEST, msg, t);
            }
            else {
                Agent.LOG.finer(msg);
            }
            return null;
        }
    }
    
    public static ExternalTimeTracker create(final Request httpRequest, final long txStartTimeInMillis) {
        return new ExternalTimeTracker(httpRequest, txStartTimeInMillis);
    }
    
    public static long parseTimestampToNano(final String strTime) throws NumberFormatException {
        final double time = Double.parseDouble(strTime);
        return parseTimestampToNano(time);
    }
    
    public static long parseTimestampToNano(final double time) throws NumberFormatException {
        if (time > 0.0) {
            for (final long magnitude : ExternalTimeTracker.MAGNITUDES) {
                final long candidate = (long)(time * magnitude);
                if (946684800000000000L < candidate && candidate < 4954167440812867584L) {
                    return candidate;
                }
            }
        }
        throw new NumberFormatException("The long " + time + " could not be converted to a timestamp in nanoseconds (wrong magnitude).");
    }
    
    static {
        MAGNITUDES = Arrays.asList(1, 1000, 1000000, 1000000000);
    }
}
