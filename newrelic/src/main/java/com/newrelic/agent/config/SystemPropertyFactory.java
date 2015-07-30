// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

public class SystemPropertyFactory
{
    private static volatile SystemPropertyProvider SYSTEM_PROPERTY_PROVIDER;
    
    public static void setSystemPropertyProvider(final SystemPropertyProvider provider) {
        SystemPropertyFactory.SYSTEM_PROPERTY_PROVIDER = provider;
    }
    
    public static SystemPropertyProvider getSystemPropertyProvider() {
        return SystemPropertyFactory.SYSTEM_PROPERTY_PROVIDER;
    }
    
    static {
        SystemPropertyFactory.SYSTEM_PROPERTY_PROVIDER = new SystemPropertyProvider();
    }
}
