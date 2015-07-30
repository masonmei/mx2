// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import java.util.Calendar;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.GregorianCalendar;

public class RollingCalendar extends GregorianCalendar
{
    private static final long serialVersionUID = -5937537740925066161L;
    static final TimeZone GMT_TIMEZONE;
    PeriodicityType periodicityType;
    
    public RollingCalendar() {
        this.periodicityType = PeriodicityType.ERRONEOUS;
    }
    
    public RollingCalendar(final TimeZone tz, final Locale locale) {
        super(tz, locale);
        this.periodicityType = PeriodicityType.ERRONEOUS;
    }
    
    public void init(final String datePattern) {
        this.periodicityType = this.computePeriodicityType(datePattern);
    }
    
    private void setPeriodicityType(final PeriodicityType periodicityType) {
        this.periodicityType = periodicityType;
    }
    
    public PeriodicityType getPeriodicityType() {
        return this.periodicityType;
    }
    
    public long getNextTriggeringMillis(final Date now) {
        return this.getNextTriggeringDate(now).getTime();
    }
    
    public PeriodicityType computePeriodicityType(final String datePattern) {
        final RollingCalendar rollingCalendar = new RollingCalendar(RollingCalendar.GMT_TIMEZONE, Locale.getDefault());
        final Date epoch = new Date(0L);
        if (datePattern != null) {
            for (final PeriodicityType i : PeriodicityType.VALID_ORDERED_LIST) {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
                simpleDateFormat.setTimeZone(RollingCalendar.GMT_TIMEZONE);
                final String r0 = simpleDateFormat.format(epoch);
                rollingCalendar.setPeriodicityType(i);
                final Date next = new Date(rollingCalendar.getNextTriggeringMillis(epoch));
                final String r = simpleDateFormat.format(next);
                if (r0 != null && r != null && !r0.equals(r)) {
                    return i;
                }
            }
        }
        return PeriodicityType.ERRONEOUS;
    }
    
    public void printPeriodicity(final ContextAwareBase cab) {
        switch (this.periodicityType) {
            case TOP_OF_MILLISECOND: {
                cab.addInfo("Roll-over every millisecond.");
                break;
            }
            case TOP_OF_SECOND: {
                cab.addInfo("Roll-over every second.");
                break;
            }
            case TOP_OF_MINUTE: {
                cab.addInfo("Roll-over every minute.");
                break;
            }
            case TOP_OF_HOUR: {
                cab.addInfo("Roll-over at the top of every hour.");
                break;
            }
            case HALF_DAY: {
                cab.addInfo("Roll-over at midday and midnight.");
                break;
            }
            case TOP_OF_DAY: {
                cab.addInfo("Roll-over at midnight.");
                break;
            }
            case TOP_OF_WEEK: {
                cab.addInfo("Rollover at the start of week.");
                break;
            }
            case TOP_OF_MONTH: {
                cab.addInfo("Rollover at start of every month.");
                break;
            }
            default: {
                cab.addInfo("Unknown periodicity.");
                break;
            }
        }
    }
    
    public long periodsElapsed(final long start, final long end) {
        if (start > end) {
            throw new IllegalArgumentException("Start cannot come before end");
        }
        final long diff = end - start;
        switch (this.periodicityType) {
            case TOP_OF_MILLISECOND: {
                return diff;
            }
            case TOP_OF_SECOND: {
                return diff / 1000L;
            }
            case TOP_OF_MINUTE: {
                return diff / 60000L;
            }
            case TOP_OF_HOUR: {
                return (int)diff / 3600000;
            }
            case TOP_OF_DAY: {
                return diff / 86400000L;
            }
            case TOP_OF_WEEK: {
                return diff / 604800000L;
            }
            case TOP_OF_MONTH: {
                return diffInMonths(start, end);
            }
            default: {
                throw new IllegalStateException("Unknown periodicity type.");
            }
        }
    }
    
    public static int diffInMonths(final long startTime, final long endTime) {
        if (startTime > endTime) {
            throw new IllegalArgumentException("startTime cannot be larger than endTime");
        }
        final Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(startTime);
        final Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(endTime);
        final int yearDiff = endCal.get(1) - startCal.get(1);
        final int monthDiff = endCal.get(2) - startCal.get(2);
        return yearDiff * 12 + monthDiff;
    }
    
    public Date getRelativeDate(final Date now, final int periods) {
        this.setTime(now);
        switch (this.periodicityType) {
            case TOP_OF_MILLISECOND: {
                this.add(14, periods);
                break;
            }
            case TOP_OF_SECOND: {
                this.set(14, 0);
                this.add(13, periods);
                break;
            }
            case TOP_OF_MINUTE: {
                this.set(13, 0);
                this.set(14, 0);
                this.add(12, periods);
                break;
            }
            case TOP_OF_HOUR: {
                this.set(12, 0);
                this.set(13, 0);
                this.set(14, 0);
                this.add(11, periods);
                break;
            }
            case TOP_OF_DAY: {
                this.set(11, 0);
                this.set(12, 0);
                this.set(13, 0);
                this.set(14, 0);
                this.add(5, periods);
                break;
            }
            case TOP_OF_WEEK: {
                this.set(7, this.getFirstDayOfWeek());
                this.set(11, 0);
                this.set(12, 0);
                this.set(13, 0);
                this.set(14, 0);
                this.add(3, periods);
                break;
            }
            case TOP_OF_MONTH: {
                this.set(5, 1);
                this.set(11, 0);
                this.set(12, 0);
                this.set(13, 0);
                this.set(14, 0);
                this.add(2, periods);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown periodicity type.");
            }
        }
        return this.getTime();
    }
    
    public Date getNextTriggeringDate(final Date now) {
        return this.getRelativeDate(now, 1);
    }
    
    static {
        GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
    }
}
