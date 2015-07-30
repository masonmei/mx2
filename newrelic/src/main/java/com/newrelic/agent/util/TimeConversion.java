// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

public class TimeConversion
{
    public static final long NANOSECONDS_PER_SECOND = 1000000000L;
    public static final long MICROSECONDS_PER_SECOND = 1000000L;
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    
    public static double convertMillisToSeconds(final double millis) {
        return millis / 1000.0;
    }
    
    public static double convertNanosToSeconds(final double nanos) {
        return nanos / 1.0E9;
    }
    
    public static long convertSecondsToMillis(final double seconds) {
        return (long)(seconds * 1000.0);
    }
    
    public static long convertSecondsToNanos(final double seconds) {
        return (long)(seconds * 1.0E9);
    }
}
