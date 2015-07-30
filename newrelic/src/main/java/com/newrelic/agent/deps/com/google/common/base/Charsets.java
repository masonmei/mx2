// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.base;

import com.newrelic.agent.deps.com.google.common.annotations.GwtIncompatible;
import java.nio.charset.Charset;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class Charsets
{
    @GwtIncompatible("Non-UTF-8 Charset")
    public static final Charset US_ASCII;
    @GwtIncompatible("Non-UTF-8 Charset")
    public static final Charset ISO_8859_1;
    public static final Charset UTF_8;
    @GwtIncompatible("Non-UTF-8 Charset")
    public static final Charset UTF_16BE;
    @GwtIncompatible("Non-UTF-8 Charset")
    public static final Charset UTF_16LE;
    @GwtIncompatible("Non-UTF-8 Charset")
    public static final Charset UTF_16;
    
    static {
        US_ASCII = Charset.forName("US-ASCII");
        ISO_8859_1 = Charset.forName("ISO-8859-1");
        UTF_8 = Charset.forName("UTF-8");
        UTF_16BE = Charset.forName("UTF-16BE");
        UTF_16LE = Charset.forName("UTF-16LE");
        UTF_16 = Charset.forName("UTF-16");
    }
}
