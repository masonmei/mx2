// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;

public class ThrowableProxyUtil
{
    public static final int REGULAR_EXCEPTION_INDENT = 1;
    public static final int SUPPRESSED_EXCEPTION_INDENT = 2;
    
    public static void build(final ThrowableProxy nestedTP, final Throwable nestedThrowable, final ThrowableProxy parentTP) {
        final StackTraceElement[] nestedSTE = nestedThrowable.getStackTrace();
        int commonFramesCount = -1;
        if (parentTP != null) {
            commonFramesCount = findNumberOfCommonFrames(nestedSTE, parentTP.getStackTraceElementProxyArray());
        }
        nestedTP.commonFrames = commonFramesCount;
        nestedTP.stackTraceElementProxyArray = steArrayToStepArray(nestedSTE);
    }
    
    static StackTraceElementProxy[] steArrayToStepArray(final StackTraceElement[] stea) {
        if (stea == null) {
            return new StackTraceElementProxy[0];
        }
        final StackTraceElementProxy[] stepa = new StackTraceElementProxy[stea.length];
        for (int i = 0; i < stepa.length; ++i) {
            stepa[i] = new StackTraceElementProxy(stea[i]);
        }
        return stepa;
    }
    
    static int findNumberOfCommonFrames(final StackTraceElement[] steArray, final StackTraceElementProxy[] parentSTEPArray) {
        if (parentSTEPArray == null || steArray == null) {
            return 0;
        }
        int steIndex = steArray.length - 1;
        int parentIndex = parentSTEPArray.length - 1;
        int count = 0;
        while (steIndex >= 0 && parentIndex >= 0) {
            final StackTraceElement ste = steArray[steIndex];
            final StackTraceElement otherSte = parentSTEPArray[parentIndex].ste;
            if (!ste.equals(otherSte)) {
                break;
            }
            ++count;
            --steIndex;
            --parentIndex;
        }
        return count;
    }
    
    public static String asString(final IThrowableProxy tp) {
        final StringBuilder sb = new StringBuilder();
        recursiveAppend(sb, null, 1, tp);
        return sb.toString();
    }
    
    private static void recursiveAppend(final StringBuilder sb, final String prefix, final int indent, final IThrowableProxy tp) {
        if (tp == null) {
            return;
        }
        subjoinFirstLine(sb, prefix, tp);
        sb.append(CoreConstants.LINE_SEPARATOR);
        subjoinSTEPArray(sb, indent, tp);
        final IThrowableProxy[] suppressed = tp.getSuppressed();
        if (suppressed != null) {
            for (final IThrowableProxy current : suppressed) {
                recursiveAppend(sb, "\tSuppressed: ", 2, current);
            }
        }
        recursiveAppend(sb, "Caused by: ", 1, tp.getCause());
    }
    
    private static void subjoinFirstLine(final StringBuilder buf, final String prefix, final IThrowableProxy tp) {
        if (prefix != null) {
            buf.append(prefix);
        }
        subjoinExceptionMessage(buf, tp);
    }
    
    public static void subjoinPackagingData(final StringBuilder builder, final StackTraceElementProxy step) {
        if (step != null) {
            final ClassPackagingData cpd = step.getClassPackagingData();
            if (cpd != null) {
                if (!cpd.isExact()) {
                    builder.append(" ~[");
                }
                else {
                    builder.append(" [");
                }
                builder.append(cpd.getCodeLocation()).append(':').append(cpd.getVersion()).append(']');
            }
        }
    }
    
    public static void subjoinSTEP(final StringBuilder sb, final StackTraceElementProxy step) {
        sb.append(step.toString());
        subjoinPackagingData(sb, step);
    }
    
    public static void subjoinSTEPArray(final StringBuilder sb, final IThrowableProxy tp) {
        subjoinSTEPArray(sb, 1, tp);
    }
    
    public static void subjoinSTEPArray(final StringBuilder sb, final int indentLevel, final IThrowableProxy tp) {
        final StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        final int commonFrames = tp.getCommonFrames();
        for (int i = 0; i < stepArray.length - commonFrames; ++i) {
            final StackTraceElementProxy step = stepArray[i];
            for (int j = 0; j < indentLevel; ++j) {
                sb.append('\t');
            }
            subjoinSTEP(sb, step);
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        if (commonFrames > 0) {
            for (int k = 0; k < indentLevel; ++k) {
                sb.append('\t');
            }
            sb.append("... ").append(commonFrames).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
        }
    }
    
    public static void subjoinFirstLine(final StringBuilder buf, final IThrowableProxy tp) {
        final int commonFrames = tp.getCommonFrames();
        if (commonFrames > 0) {
            buf.append("Caused by: ");
        }
        subjoinExceptionMessage(buf, tp);
    }
    
    public static void subjoinFirstLineRootCauseFirst(final StringBuilder buf, final IThrowableProxy tp) {
        if (tp.getCause() != null) {
            buf.append("Wrapped by: ");
        }
        subjoinExceptionMessage(buf, tp);
    }
    
    private static void subjoinExceptionMessage(final StringBuilder buf, final IThrowableProxy tp) {
        buf.append(tp.getClassName()).append(": ").append(tp.getMessage());
    }
}
