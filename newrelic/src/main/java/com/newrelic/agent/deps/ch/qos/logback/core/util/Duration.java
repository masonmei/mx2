// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Duration
{
    private static final String DOUBLE_PART = "([0-9]*(.[0-9]+)?)";
    private static final int DOUBLE_GROUP = 1;
    private static final String UNIT_PART = "(|milli(second)?|second(e)?|minute|hour|day)s?";
    private static final int UNIT_GROUP = 3;
    private static final Pattern DURATION_PATTERN;
    static final long SECONDS_COEFFICIENT = 1000L;
    static final long MINUTES_COEFFICIENT = 60000L;
    static final long HOURS_COEFFICIENT = 3600000L;
    static final long DAYS_COEFFICIENT = 86400000L;
    final long millis;
    
    public Duration(final long millis) {
        this.millis = millis;
    }
    
    public static Duration buildByMilliseconds(final double value) {
        return new Duration((long)value);
    }
    
    public static Duration buildBySeconds(final double value) {
        return new Duration((long)(1000.0 * value));
    }
    
    public static Duration buildByMinutes(final double value) {
        return new Duration((long)(60000.0 * value));
    }
    
    public static Duration buildByHours(final double value) {
        return new Duration((long)(3600000.0 * value));
    }
    
    public static Duration buildByDays(final double value) {
        return new Duration((long)(8.64E7 * value));
    }
    
    public static Duration buildUnbounded() {
        return new Duration(Long.MAX_VALUE);
    }
    
    public long getMilliseconds() {
        return this.millis;
    }
    
    public static Duration valueOf(final String durationStr) {
        final Matcher matcher = Duration.DURATION_PATTERN.matcher(durationStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("String value [" + durationStr + "] is not in the expected format.");
        }
        final String doubleStr = matcher.group(1);
        final String unitStr = matcher.group(3);
        final double doubleValue = Double.valueOf(doubleStr);
        if (unitStr.equalsIgnoreCase("milli") || unitStr.equalsIgnoreCase("millisecond") || unitStr.length() == 0) {
            return buildByMilliseconds(doubleValue);
        }
        if (unitStr.equalsIgnoreCase("second") || unitStr.equalsIgnoreCase("seconde")) {
            return buildBySeconds(doubleValue);
        }
        if (unitStr.equalsIgnoreCase("minute")) {
            return buildByMinutes(doubleValue);
        }
        if (unitStr.equalsIgnoreCase("hour")) {
            return buildByHours(doubleValue);
        }
        if (unitStr.equalsIgnoreCase("day")) {
            return buildByDays(doubleValue);
        }
        throw new IllegalStateException("Unexpected " + unitStr);
    }
    
    public String toString() {
        if (this.millis < 1000L) {
            return this.millis + " milliseconds";
        }
        if (this.millis < 60000L) {
            return this.millis / 1000L + " seconds";
        }
        if (this.millis < 3600000L) {
            return this.millis / 60000L + " minutes";
        }
        return this.millis / 3600000L + " hours";
    }
    
    static {
        DURATION_PATTERN = Pattern.compile("([0-9]*(.[0-9]+)?)\\s*(|milli(second)?|second(e)?|minute|hour|day)s?", 2);
    }
}
