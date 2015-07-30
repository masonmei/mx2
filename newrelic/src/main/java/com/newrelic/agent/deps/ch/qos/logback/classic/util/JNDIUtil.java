// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.util;

import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.naming.Context;

public class JNDIUtil
{
    public static Context getInitialContext() throws NamingException {
        return new InitialContext();
    }
    
    public static String lookup(final Context ctx, final String name) {
        if (ctx == null) {
            return null;
        }
        try {
            return (String)ctx.lookup(name);
        }
        catch (NamingException e) {
            return null;
        }
    }
}
