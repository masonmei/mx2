// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

import java.util.Date;
import java.util.Calendar;

public class TimeUtil
{
    public static long computeStartOfNextSecond(final long now) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(now));
        cal.set(14, 0);
        cal.add(13, 1);
        return cal.getTime().getTime();
    }
    
    public static long computeStartOfNextMinute(final long now) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(now));
        cal.set(14, 0);
        cal.set(13, 0);
        cal.add(12, 1);
        return cal.getTime().getTime();
    }
    
    public static long computeStartOfNextHour(final long now) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(now));
        cal.set(14, 0);
        cal.set(13, 0);
        cal.set(12, 0);
        cal.add(10, 1);
        return cal.getTime().getTime();
    }
    
    public static long computeStartOfNextDay(final long now) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(now));
        cal.add(5, 1);
        cal.set(14, 0);
        cal.set(13, 0);
        cal.set(12, 0);
        cal.set(11, 0);
        return cal.getTime().getTime();
    }
    
    public static long computeStartOfNextWeek(final long now) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(now));
        cal.set(7, cal.getFirstDayOfWeek());
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        cal.add(3, 1);
        return cal.getTime().getTime();
    }
    
    public static long computeStartOfNextMonth(final long now) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(now));
        cal.set(5, 1);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        cal.add(2, 1);
        return cal.getTime().getTime();
    }
}
