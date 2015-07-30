// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern.util;

public class RegularEscapeUtil implements IEscapeUtil
{
    public void escape(final String escapeChars, final StringBuffer buf, final char next, final int pointer) {
        if (escapeChars.indexOf(next) >= 0) {
            buf.append(next);
        }
        else {
            switch (next) {
                case '_': {
                    break;
                }
                case '\\': {
                    buf.append(next);
                    break;
                }
                case 't': {
                    buf.append('\t');
                    break;
                }
                case 'r': {
                    buf.append('\r');
                    break;
                }
                case 'n': {
                    buf.append('\n');
                    break;
                }
                default: {
                    final String commaSeperatedEscapeChars = this.formatEscapeCharsForListing(escapeChars);
                    throw new IllegalArgumentException("Illegal char '" + next + " at column " + pointer + ". Only \\\\, \\_" + commaSeperatedEscapeChars + ", \\t, \\n, \\r combinations are allowed as escape characters.");
                }
            }
        }
    }
    
    String formatEscapeCharsForListing(final String escapeChars) {
        final StringBuilder commaSeperatedEscapeChars = new StringBuilder();
        for (int i = 0; i < escapeChars.length(); ++i) {
            commaSeperatedEscapeChars.append(", \\").append(escapeChars.charAt(i));
        }
        return commaSeperatedEscapeChars.toString();
    }
    
    public static String basicEscape(final String s) {
        final int len = s.length();
        final StringBuffer sbuf = new StringBuffer(len);
        int i = 0;
        while (i < len) {
            char c = s.charAt(i++);
            if (c == '\\') {
                c = s.charAt(i++);
                if (c == 'n') {
                    c = '\n';
                }
                else if (c == 'r') {
                    c = '\r';
                }
                else if (c == 't') {
                    c = '\t';
                }
                else if (c == 'f') {
                    c = '\f';
                }
                else if (c == '\b') {
                    c = '\b';
                }
                else if (c == '\"') {
                    c = '\"';
                }
                else if (c == '\'') {
                    c = '\'';
                }
                else if (c == '\\') {
                    c = '\\';
                }
            }
            sbuf.append(c);
        }
        return sbuf.toString();
    }
}
