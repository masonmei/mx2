// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

public class ContentTypeUtil
{
    public static boolean isTextual(final String contextType) {
        return contextType != null && contextType.startsWith("text");
    }
    
    public static String getSubType(final String contextType) {
        if (contextType == null) {
            return null;
        }
        final int index = contextType.indexOf(47);
        if (index == -1) {
            return null;
        }
        final int subTypeStartIndex = index + 1;
        if (subTypeStartIndex < contextType.length()) {
            return contextType.substring(subTypeStartIndex);
        }
        return null;
    }
}
