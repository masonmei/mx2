// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.helpers;

import java.util.List;
import java.util.LinkedList;

public class ThrowableToStringArray
{
    public static String[] convert(final Throwable t) {
        final List<String> strList = new LinkedList<String>();
        extract(strList, t, null);
        return strList.toArray(new String[0]);
    }
    
    private static void extract(final List<String> strList, final Throwable t, final StackTraceElement[] parentSTE) {
        final StackTraceElement[] ste = t.getStackTrace();
        final int numberOfcommonFrames = findNumberOfCommonFrames(ste, parentSTE);
        strList.add(formatFirstLine(t, parentSTE));
        for (int i = 0; i < ste.length - numberOfcommonFrames; ++i) {
            strList.add("\tat " + ste[i].toString());
        }
        if (numberOfcommonFrames != 0) {
            strList.add("\t... " + numberOfcommonFrames + " common frames omitted");
        }
        final Throwable cause = t.getCause();
        if (cause != null) {
            extract(strList, cause, ste);
        }
    }
    
    private static String formatFirstLine(final Throwable t, final StackTraceElement[] parentSTE) {
        String prefix = "";
        if (parentSTE != null) {
            prefix = "Caused by: ";
        }
        String result = prefix + t.getClass().getName();
        if (t.getMessage() != null) {
            result = result + ": " + t.getMessage();
        }
        return result;
    }
    
    private static int findNumberOfCommonFrames(final StackTraceElement[] ste, final StackTraceElement[] parentSTE) {
        if (parentSTE == null) {
            return 0;
        }
        int steIndex = ste.length - 1;
        int parentIndex = parentSTE.length - 1;
        int count = 0;
        while (steIndex >= 0 && parentIndex >= 0 && ste[steIndex].equals(parentSTE[parentIndex])) {
            ++count;
            --steIndex;
            --parentIndex;
        }
        return count;
    }
}
