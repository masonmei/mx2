// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

public class PlatformInfo
{
    private static final int UNINITIALIZED = -1;
    private static int hasJMXObjectName;
    
    public static boolean hasJMXObjectName() {
        if (PlatformInfo.hasJMXObjectName == -1) {
            try {
                Class.forName("javax.management.ObjectName");
                PlatformInfo.hasJMXObjectName = 1;
            }
            catch (Throwable e) {
                PlatformInfo.hasJMXObjectName = 0;
            }
        }
        return PlatformInfo.hasJMXObjectName == 1;
    }
    
    static {
        PlatformInfo.hasJMXObjectName = -1;
    }
}
