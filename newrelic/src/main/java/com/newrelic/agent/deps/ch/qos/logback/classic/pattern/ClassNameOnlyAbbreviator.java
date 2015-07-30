// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

public class ClassNameOnlyAbbreviator implements Abbreviator
{
    public String abbreviate(final String fqClassName) {
        final int lastIndex = fqClassName.lastIndexOf(46);
        if (lastIndex != -1) {
            return fqClassName.substring(lastIndex + 1, fqClassName.length());
        }
        return fqClassName;
    }
}
