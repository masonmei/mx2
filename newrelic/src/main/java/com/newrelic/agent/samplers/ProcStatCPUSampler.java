// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.samplers;

import java.text.MessageFormat;
import java.io.OutputStream;
import java.io.InputStream;
import com.newrelic.agent.util.Streams;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.newrelic.agent.Agent;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import java.io.File;

public class ProcStatCPUSampler extends AbstractCPUSampler
{
    private final File statFile;
    private final long clockTicksPerSecond;
    
    public ProcStatCPUSampler(final File statFile) throws Exception {
        this.statFile = statFile;
        this.clockTicksPerSecond = this.getClockTicksPerSecond();
        this.readCPUStats();
    }
    
    private long getClockTicksPerSecond() {
        final long defaultClockTicks = 100L;
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        return config.getProperty("clock_ticks_per_second", defaultClockTicks);
    }
    
    protected double getProcessCpuTime() {
        try {
            final CPUStats stats = this.readCPUStats();
            Agent.LOG.finest("CPU Stats " + stats);
            if (stats == null) {
                return 0.0;
            }
            return stats.getSystemTime() + stats.getUserTime();
        }
        catch (IOException e) {
            return 0.0;
        }
    }
    
    private CPUStats readCPUStats() throws IOException {
        final ByteArrayOutputStream oStream = new ByteArrayOutputStream(8192);
        final FileInputStream iStream = new FileInputStream(this.statFile);
        String userTime = "";
        String systemTime = "";
        try {
            Streams.copy(iStream, oStream);
            oStream.close();
            final String[] stats = oStream.toString().split(" ");
            if (stats.length > 13) {
                userTime = stats[13];
                systemTime = stats[14];
                final CPUStats cpuStats = new CPUStats(Long.parseLong(userTime), Long.parseLong(systemTime));
                try {
                    iStream.close();
                }
                catch (IOException ex) {}
                return cpuStats;
            }
            try {
                iStream.close();
            }
            catch (IOException ex2) {}
        }
        catch (NumberFormatException e) {
            Agent.LOG.fine(MessageFormat.format("Badly formatted CPU jiffies: ''{0}'' user, ''{1}'' system", userTime, systemTime));
            final CPUStats cpuStats2 = null;
            try {
                iStream.close();
            }
            catch (IOException ex3) {}
            return cpuStats2;
        }
        finally {
            try {
                iStream.close();
            }
            catch (IOException ex4) {}
        }
        return null;
    }
    
    private class CPUStats
    {
        private final double userTime;
        private final double systemTime;
        
        public CPUStats(final long userTime, final long systemTime) {
            this.userTime = userTime / ProcStatCPUSampler.this.clockTicksPerSecond;
            this.systemTime = systemTime / ProcStatCPUSampler.this.clockTicksPerSecond;
        }
        
        public double getUserTime() {
            return this.userTime;
        }
        
        public double getSystemTime() {
            return this.systemTime;
        }
        
        public String toString() {
            return "User: " + this.userTime + ", System: " + this.systemTime;
        }
    }
}
