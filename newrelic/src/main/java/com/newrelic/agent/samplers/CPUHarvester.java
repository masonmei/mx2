// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.samplers;

import com.newrelic.agent.util.TimeConversion;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class CPUHarvester extends AbstractCPUSampler
{
    private final OperatingSystemMXBean osMBean;
    
    public CPUHarvester() {
        this.osMBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
    }
    
    protected double getProcessCpuTime() {
        return TimeConversion.convertNanosToSeconds(this.osMBean.getProcessCpuTime());
    }
}
