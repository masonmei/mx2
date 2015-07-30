// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;

public class ProfilingTaskControllerFactory
{
    public static ProfilingTaskController createProfilingTaskController(final ProfilingTask profilingTask) {
        if (isThreadCpuTimeSupportedAndEnabled()) {
            return new XrayCpuTimeController(profilingTask);
        }
        return new XrayClockTimeController(profilingTask);
    }
    
    private static boolean isThreadCpuTimeSupportedAndEnabled() {
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        return threadMXBean.isThreadCpuTimeSupported() && threadMXBean.isThreadCpuTimeEnabled();
    }
}
