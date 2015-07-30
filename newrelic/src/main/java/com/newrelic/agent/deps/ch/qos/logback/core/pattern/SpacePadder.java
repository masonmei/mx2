// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern;

public class SpacePadder
{
    static final String[] SPACES;
    
    public static final void leftPad(final StringBuilder buf, final String s, final int desiredLength) {
        int actualLen = 0;
        if (s != null) {
            actualLen = s.length();
        }
        if (actualLen < desiredLength) {
            spacePad(buf, desiredLength - actualLen);
        }
        if (s != null) {
            buf.append(s);
        }
    }
    
    public static final void rightPad(final StringBuilder buf, final String s, final int desiredLength) {
        int actualLen = 0;
        if (s != null) {
            actualLen = s.length();
        }
        if (s != null) {
            buf.append(s);
        }
        if (actualLen < desiredLength) {
            spacePad(buf, desiredLength - actualLen);
        }
    }
    
    public static final void spacePad(final StringBuilder sbuf, int length) {
        while (length >= 32) {
            sbuf.append(SpacePadder.SPACES[5]);
            length -= 32;
        }
        for (int i = 4; i >= 0; --i) {
            if ((length & 1 << i) != 0x0) {
                sbuf.append(SpacePadder.SPACES[i]);
            }
        }
    }
    
    static {
        SPACES = new String[] { " ", "  ", "    ", "        ", "                ", "                                " };
    }
}
