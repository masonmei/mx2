// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

public class DynamicClassLoadingException extends Exception
{
    private static final long serialVersionUID = 4962278449162476114L;
    
    public DynamicClassLoadingException(final String desc, final Throwable root) {
        super(desc, root);
    }
}
