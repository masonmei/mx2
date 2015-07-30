// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.utils;

import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class Punycode
{
    private static final Idn impl;
    
    public static String toUnicode(final String punycode) {
        return Punycode.impl.toUnicode(punycode);
    }
    
    static {
        Idn _impl;
        try {
            _impl = new JdkIdn();
        }
        catch (Exception e) {
            _impl = new Rfc3492Idn();
        }
        impl = _impl;
    }
}
