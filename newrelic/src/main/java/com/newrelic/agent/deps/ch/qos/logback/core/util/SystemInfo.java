// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

public class SystemInfo
{
    public static String getJavaVendor() {
        return OptionHelper.getSystemProperty("java.vendor", null);
    }
}
