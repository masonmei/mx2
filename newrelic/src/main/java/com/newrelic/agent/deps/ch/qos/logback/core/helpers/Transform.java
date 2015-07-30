// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.helpers;

public class Transform
{
    private static final String CDATA_START = "<![CDATA[";
    private static final String CDATA_END = "]]>";
    private static final String CDATA_PSEUDO_END = "]]&gt;";
    private static final String CDATA_EMBEDED_END = "]]>]]&gt;<![CDATA[";
    private static final int CDATA_END_LEN;
    
    public static String escapeTags(final String input) {
        if (input == null || input.length() == 0 || (input.indexOf("<") == -1 && input.indexOf(">") == -1)) {
            return input;
        }
        final StringBuffer buf = new StringBuffer(input);
        return escapeTags(buf);
    }
    
    public static String escapeTags(final StringBuffer buf) {
        for (int i = 0; i < buf.length(); ++i) {
            final char ch = buf.charAt(i);
            if (ch == '<') {
                buf.replace(i, i + 1, "&lt;");
            }
            else if (ch == '>') {
                buf.replace(i, i + 1, "&gt;");
            }
        }
        return buf.toString();
    }
    
    public static void appendEscapingCDATA(final StringBuilder output, final String str) {
        if (str == null) {
            return;
        }
        int end = str.indexOf("]]>");
        if (end < 0) {
            output.append(str);
            return;
        }
        int start;
        for (start = 0; end > -1; end = str.indexOf("]]>", start)) {
            output.append(str.substring(start, end));
            output.append("]]>]]&gt;<![CDATA[");
            start = end + Transform.CDATA_END_LEN;
            if (start >= str.length()) {
                return;
            }
        }
        output.append(str.substring(start));
    }
    
    static {
        CDATA_END_LEN = "]]>".length();
    }
}
