// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

public class STEUtil
{
    static int UNUSED_findNumberOfCommonFrames(final StackTraceElement[] steArray, final StackTraceElement[] otherSTEArray) {
        if (otherSTEArray == null) {
            return 0;
        }
        int steIndex = steArray.length - 1;
        int parentIndex = otherSTEArray.length - 1;
        int count = 0;
        while (steIndex >= 0 && parentIndex >= 0 && steArray[steIndex].equals(otherSTEArray[parentIndex])) {
            ++count;
            --steIndex;
            --parentIndex;
        }
        return count;
    }
    
    static int findNumberOfCommonFrames(final StackTraceElement[] steArray, final StackTraceElementProxy[] otherSTEPArray) {
        if (otherSTEPArray == null) {
            return 0;
        }
        int steIndex = steArray.length - 1;
        int parentIndex = otherSTEPArray.length - 1;
        int count = 0;
        while (steIndex >= 0 && parentIndex >= 0 && steArray[steIndex].equals(otherSTEPArray[parentIndex].ste)) {
            ++count;
            --steIndex;
            --parentIndex;
        }
        return count;
    }
}
