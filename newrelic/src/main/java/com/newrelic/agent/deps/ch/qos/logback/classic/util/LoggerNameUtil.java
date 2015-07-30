// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.util;

import java.util.ArrayList;
import java.util.List;

public class LoggerNameUtil
{
    public static int getFirstSeparatorIndexOf(final String name) {
        return getSeparatorIndexOf(name, 0);
    }
    
    public static int getSeparatorIndexOf(final String name, final int fromIndex) {
        final int i = name.indexOf(46, fromIndex);
        if (i != -1) {
            return i;
        }
        return name.indexOf(36, fromIndex);
    }
    
    public static List<String> computeNameParts(final String loggerName) {
        final List<String> partList = new ArrayList<String>();
        int fromIndex = 0;
        while (true) {
            final int index = getSeparatorIndexOf(loggerName, fromIndex);
            if (index == -1) {
                break;
            }
            partList.add(loggerName.substring(fromIndex, index));
            fromIndex = index + 1;
        }
        partList.add(loggerName.substring(fromIndex));
        return partList;
    }
}
