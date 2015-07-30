// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;

public class CallerData
{
    public static final String NA = "?";
    private static final String LOG4J_CATEGORY = "org.apache.log4j.Category";
    public static final int LINE_NA = -1;
    public static final String CALLER_DATA_NA;
    public static final StackTraceElement[] EMPTY_CALLER_DATA_ARRAY;
    
    public static StackTraceElement[] extract(final Throwable t, final String fqnOfInvokingClass, final int maxDepth) {
        if (t == null) {
            return null;
        }
        final StackTraceElement[] steArray = t.getStackTrace();
        int found = -1;
        for (int i = 0; i < steArray.length; ++i) {
            if (isDirectlyInvokingClass(steArray[i].getClassName(), fqnOfInvokingClass)) {
                found = i + 1;
            }
            else if (found != -1) {
                break;
            }
        }
        if (found == -1) {
            return CallerData.EMPTY_CALLER_DATA_ARRAY;
        }
        final int availableDepth = steArray.length - found;
        final int desiredDepth = (maxDepth < availableDepth) ? maxDepth : availableDepth;
        final StackTraceElement[] callerDataArray = new StackTraceElement[desiredDepth];
        for (int j = 0; j < desiredDepth; ++j) {
            callerDataArray[j] = steArray[found + j];
        }
        return callerDataArray;
    }
    
    public static boolean isDirectlyInvokingClass(final String currentClass, final String fqnOfInvokingClass) {
        return currentClass.equals(fqnOfInvokingClass) || currentClass.equals("org.apache.log4j.Category");
    }
    
    static {
        CALLER_DATA_NA = "?#?:?" + CoreConstants.LINE_SEPARATOR;
        EMPTY_CALLER_DATA_ARRAY = new StackTraceElement[0];
    }
}
