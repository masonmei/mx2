// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import java.util.Date;
import java.util.TimeZone;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Deprecated
@Immutable
public final class DateUtils
{
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    public static final TimeZone GMT;
    
    public static Date parseDate(final String dateValue) throws DateParseException {
        return parseDate(dateValue, null, null);
    }
    
    public static Date parseDate(final String dateValue, final String[] dateFormats) throws DateParseException {
        return parseDate(dateValue, dateFormats, null);
    }
    
    public static Date parseDate(final String dateValue, final String[] dateFormats, final Date startDate) throws DateParseException {
        final Date d = com.newrelic.agent.deps.org.apache.http.client.utils.DateUtils.parseDate(dateValue, dateFormats, startDate);
        if (d == null) {
            throw new DateParseException("Unable to parse the date " + dateValue);
        }
        return d;
    }
    
    public static String formatDate(final Date date) {
        return com.newrelic.agent.deps.org.apache.http.client.utils.DateUtils.formatDate(date);
    }
    
    public static String formatDate(final Date date, final String pattern) {
        return com.newrelic.agent.deps.org.apache.http.client.utils.DateUtils.formatDate(date, pattern);
    }
    
    static {
        GMT = TimeZone.getTimeZone("GMT");
    }
}
