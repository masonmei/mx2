// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.Collections;
import java.util.Collection;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.lang.management.ThreadInfo;
import java.lang.management.ManagementFactory;

public class StackTraces
{
    public static StackTraceElement[] getThreadStackTraceElements(final long threadId) {
        final ThreadInfo threadInfo = ManagementFactory.getThreadMXBean().getThreadInfo(threadId, Integer.MAX_VALUE);
        if (threadInfo == null) {
            return null;
        }
        return threadInfo.getStackTrace();
    }
    
    public static Exception createStackTraceException(final String message) {
        final StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        return createStackTraceException(message, stackTraces, true);
    }
    
    public static Exception createStackTraceException(final String message, final StackTraceElement[] stackTraces, final boolean scrub) {
        return createStackTraceException(new Exception(message), stackTraces, scrub);
    }
    
    public static Exception createStackTraceException(final Exception e, final StackTraceElement[] stackTraces, final boolean scrub) {
        final List<StackTraceElement> scrubbedTrace = scrub ? scrubAndTruncate(stackTraces) : Arrays.asList(stackTraces);
        e.setStackTrace(scrubbedTrace.toArray(new StackTraceElement[0]));
        return e;
    }
    
    public static List<String> toStringList(final List<StackTraceElement> stackElements) {
        final List<String> stringList = new ArrayList<String>(stackElements.size());
        for (final StackTraceElement element : stackElements) {
            stringList.add(element.toString());
        }
        return stringList;
    }
    
    public static List<String> toStringListRemoveParent(final List<StackTraceElement> stackElements, List<StackTraceElement> parentBacktrace) {
        if (parentBacktrace == null || parentBacktrace.size() <= 1) {
            return toStringList(stackElements);
        }
        parentBacktrace = scrubAndTruncate(parentBacktrace);
        if (parentBacktrace == null || parentBacktrace.size() <= 1) {
            return toStringList(stackElements);
        }
        final StackTraceElement parentLatestFirst = parentBacktrace.get(0);
        final StackTraceElement parentLatestSecond = parentBacktrace.get(1);
        final List<String> stringList = new ArrayList<String>();
        for (int currentLength = stackElements.size(), i = 0; i < currentLength; ++i) {
            final StackTraceElement current = stackElements.get(i);
            if (isSameClassAndMethod(current, parentLatestFirst) && i + 1 < currentLength && stackElements.get(i + 1).equals(parentLatestSecond)) {
                break;
            }
            stringList.add(current.toString());
        }
        return stringList;
    }
    
    protected static boolean isSameClassAndMethod(final StackTraceElement one, final StackTraceElement two) {
        return one == two || (one.getClassName().equals(two.getClassName()) && one.getMethodName().equals(two.getMethodName()));
    }
    
    public static List<StackTraceElement> scrubAndTruncate(final StackTraceElement[] stackTraces) {
        return scrubAndTruncate(Arrays.asList(stackTraces));
    }
    
    public static List<StackTraceElement> scrubAndTruncate(final List<StackTraceElement> stackTraces) {
        return scrubAndTruncate(stackTraces, ServiceFactory.getConfigService().getDefaultAgentConfig().getMaxStackTraceLines());
    }
    
    public static List<StackTraceElement> scrubAndTruncate(final List<StackTraceElement> stackTraces, final int maxStackTraceLines) {
        final List<StackTraceElement> trimmedList = scrub(stackTraces);
        return (maxStackTraceLines > 0) ? truncateStack(trimmedList, maxStackTraceLines) : trimmedList;
    }
    
    public static List<StackTraceElement> scrub(final List<StackTraceElement> stackTraces) {
        for (int i = stackTraces.size() - 1; i >= 0; --i) {
            final StackTraceElement element = stackTraces.get(i);
            if (element.getClassName().startsWith("com.newrelic.agent.") || element.getClassName().startsWith("com.newrelic.bootstrap.") || element.getClassName().startsWith("com.newrelic.api.agent.") || ("getAgentHandle".equals(element.getMethodName()) && "java.lang.reflect.Proxy".equals(element.getClassName()))) {
                return stackTraces.subList(i + 1, stackTraces.size());
            }
        }
        return stackTraces;
    }
    
    public static List<StackTraceElement> last(final StackTraceElement[] elements, final int count) {
        final List<StackTraceElement> list = Arrays.asList(elements);
        if (list.size() <= count) {
            return list;
        }
        return list.subList(list.size() - count, list.size());
    }
    
    static List<StackTraceElement> truncateStack(List<StackTraceElement> elements, final int maxDepth) {
        if (elements.size() <= maxDepth) {
            return elements;
        }
        final int bottomLimit = Double.valueOf(Math.floor(maxDepth / 3)).intValue();
        final int topLimit = maxDepth - bottomLimit;
        final List<StackTraceElement> topStack = elements.subList(0, topLimit);
        final List<StackTraceElement> bottomStack = elements.subList(elements.size() - bottomLimit, elements.size());
        final int skipCount = elements.size() - bottomLimit - topLimit;
        elements = new ArrayList<StackTraceElement>(maxDepth + 1);
        elements.addAll(topStack);
        elements.add(new StackTraceElement("Skipping " + skipCount + " lines...", "", "", 0));
        elements.addAll(bottomStack);
        return elements;
    }
    
    public static Throwable getRootCause(final Throwable throwable) {
        return (throwable.getCause() == null) ? throwable : throwable.getCause();
    }
    
    public static Collection<String> stackTracesToStrings(final StackTraceElement[] stackTraces) {
        if (stackTraces == null || stackTraces.length == 0) {
            return Collections.emptyList();
        }
        final List<String> lines = new ArrayList<String>(stackTraces.length);
        for (final StackTraceElement e : stackTraces) {
            lines.add('\t' + e.toString());
        }
        return lines;
    }
    
    public static boolean isInAgentInstrumentation(final StackTraceElement[] stackTrace) {
        for (final StackTraceElement element : stackTrace) {
            if (element.getClassName().startsWith("com.newrelic.agent.")) {
                return true;
            }
        }
        return false;
    }
}
