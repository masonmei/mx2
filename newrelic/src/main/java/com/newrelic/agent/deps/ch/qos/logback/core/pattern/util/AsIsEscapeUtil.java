// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern.util;

public class AsIsEscapeUtil implements IEscapeUtil
{
    public void escape(final String escapeChars, final StringBuffer buf, final char next, final int pointer) {
        buf.append("\\");
        buf.append(next);
    }
}
