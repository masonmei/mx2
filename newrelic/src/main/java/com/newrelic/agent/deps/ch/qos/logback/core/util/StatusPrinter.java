// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import com.newrelic.agent.deps.ch.qos.logback.core.helpers.ThrowableToStringArray;
import java.util.Iterator;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusChecker;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.io.PrintStream;

public class StatusPrinter
{
    private static PrintStream ps;
    static CachingDateFormatter cachingDateFormat;
    
    public static void setPrintStream(final PrintStream printStream) {
        StatusPrinter.ps = printStream;
    }
    
    public static void printInCaseOfErrorsOrWarnings(final Context context) {
        printInCaseOfErrorsOrWarnings(context, 0L);
    }
    
    public static void printInCaseOfErrorsOrWarnings(final Context context, final long threshold) {
        if (context == null) {
            throw new IllegalArgumentException("Context argument cannot be null");
        }
        final StatusManager sm = context.getStatusManager();
        if (sm == null) {
            StatusPrinter.ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
        }
        else {
            final StatusChecker sc = new StatusChecker(context);
            if (sc.getHighestLevel(threshold) >= 1) {
                print(sm, threshold);
            }
        }
    }
    
    public static void printIfErrorsOccured(final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context argument cannot be null");
        }
        final StatusManager sm = context.getStatusManager();
        if (sm == null) {
            StatusPrinter.ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
        }
        else {
            final StatusChecker sc = new StatusChecker(context);
            if (sc.getHighestLevel(0L) == 2) {
                print(sm);
            }
        }
    }
    
    public static void print(final Context context) {
        print(context, 0L);
    }
    
    public static void print(final Context context, final long threshold) {
        if (context == null) {
            throw new IllegalArgumentException("Context argument cannot be null");
        }
        final StatusManager sm = context.getStatusManager();
        if (sm == null) {
            StatusPrinter.ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
        }
        else {
            print(sm, threshold);
        }
    }
    
    public static void print(final StatusManager sm) {
        print(sm, 0L);
    }
    
    public static void print(final StatusManager sm, final long threshold) {
        final StringBuilder sb = new StringBuilder();
        final List<Status> filteredList = StatusUtil.filterStatusListByTimeThreshold(sm.getCopyOfStatusList(), threshold);
        buildStrFromStatusList(sb, filteredList);
        StatusPrinter.ps.println(sb.toString());
    }
    
    public static void print(final List<Status> statusList) {
        final StringBuilder sb = new StringBuilder();
        buildStrFromStatusList(sb, statusList);
        StatusPrinter.ps.println(sb.toString());
    }
    
    private static void buildStrFromStatusList(final StringBuilder sb, final List<Status> statusList) {
        if (statusList == null) {
            return;
        }
        for (final Status s : statusList) {
            buildStr(sb, "", s);
        }
    }
    
    private static void appendThrowable(final StringBuilder sb, final Throwable t) {
        final String[] arr$;
        final String[] stringRep = arr$ = ThrowableToStringArray.convert(t);
        for (final String s : arr$) {
            if (!s.startsWith("Caused by: ")) {
                if (Character.isDigit(s.charAt(0))) {
                    sb.append("\t... ");
                }
                else {
                    sb.append("\tat ");
                }
            }
            sb.append(s).append(CoreConstants.LINE_SEPARATOR);
        }
    }
    
    public static void buildStr(final StringBuilder sb, final String indentation, final Status s) {
        String prefix;
        if (s.hasChildren()) {
            prefix = indentation + "+ ";
        }
        else {
            prefix = indentation + "|-";
        }
        if (StatusPrinter.cachingDateFormat != null) {
            final String dateStr = StatusPrinter.cachingDateFormat.format(s.getDate());
            sb.append(dateStr).append(" ");
        }
        sb.append(prefix).append(s).append(CoreConstants.LINE_SEPARATOR);
        if (s.getThrowable() != null) {
            appendThrowable(sb, s.getThrowable());
        }
        if (s.hasChildren()) {
            for (final Status child : s) {
                buildStr(sb, indentation + "  ", child);
            }
        }
    }
    
    static {
        StatusPrinter.ps = System.out;
        StatusPrinter.cachingDateFormat = new CachingDateFormatter("HH:mm:ss,SSS");
    }
}
