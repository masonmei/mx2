// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

public class TargetLengthBasedClassNameAbbreviator implements Abbreviator
{
    final int targetLength;
    
    public TargetLengthBasedClassNameAbbreviator(final int targetLength) {
        this.targetLength = targetLength;
    }
    
    public String abbreviate(final String fqClassName) {
        final StringBuilder buf = new StringBuilder(this.targetLength);
        if (fqClassName == null) {
            throw new IllegalArgumentException("Class name may not be null");
        }
        final int inLen = fqClassName.length();
        if (inLen < this.targetLength) {
            return fqClassName;
        }
        final int[] dotIndexesArray = new int[16];
        final int[] lengthArray = new int[17];
        final int dotCount = computeDotIndexes(fqClassName, dotIndexesArray);
        if (dotCount == 0) {
            return fqClassName;
        }
        this.computeLengthArray(fqClassName, dotIndexesArray, lengthArray, dotCount);
        for (int i = 0; i <= dotCount; ++i) {
            if (i == 0) {
                buf.append(fqClassName.substring(0, lengthArray[i] - 1));
            }
            else {
                buf.append(fqClassName.substring(dotIndexesArray[i - 1], dotIndexesArray[i - 1] + lengthArray[i]));
            }
        }
        return buf.toString();
    }
    
    static int computeDotIndexes(final String className, final int[] dotArray) {
        int dotCount = 0;
        int k = 0;
        while (true) {
            k = className.indexOf(46, k);
            if (k == -1 || dotCount >= 16) {
                break;
            }
            dotArray[dotCount] = k;
            ++dotCount;
            ++k;
        }
        return dotCount;
    }
    
    void computeLengthArray(final String className, final int[] dotArray, final int[] lengthArray, final int dotCount) {
        int toTrim = className.length() - this.targetLength;
        for (int i = 0; i < dotCount; ++i) {
            int previousDotPosition = -1;
            if (i > 0) {
                previousDotPosition = dotArray[i - 1];
            }
            final int available = dotArray[i] - previousDotPosition - 1;
            int len = (available < 1) ? available : 1;
            if (toTrim > 0) {
                len = ((available < 1) ? available : 1);
            }
            else {
                len = available;
            }
            toTrim -= available - len;
            lengthArray[i] = len + 1;
        }
        final int lastDotIndex = dotCount - 1;
        lengthArray[dotCount] = className.length() - dotArray[lastDotIndex];
    }
    
    static void printArray(final String msg, final int[] ia) {
        System.out.print(msg);
        for (int i = 0; i < ia.length; ++i) {
            if (i == 0) {
                System.out.print(ia[i]);
            }
            else {
                System.out.print(", " + ia[i]);
            }
        }
        System.out.println();
    }
}
