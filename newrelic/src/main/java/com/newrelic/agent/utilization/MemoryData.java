// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.utilization;

import java.util.regex.Matcher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.regex.Pattern;

public class MemoryData
{
    protected static final Pattern LINUX_MEMORY_PATTERN;
    private static Process process;
    
    public static long getTotalRamInMib() {
        final String os = ManagementFactory.getOperatingSystemMXBean().getName();
        if (os.contains("Linux")) {
            final String match = findMatchInFile(new File("/proc/meminfo"), MemoryData.LINUX_MEMORY_PATTERN);
            if (match != null) {
                final long ramInkB = parseLongRam(match);
                return ramInkB / 1024L;
            }
        }
        else {
            if (os.contains("BSD")) {
                final String output = executeCommand("sysctl -n hw.realmem");
                final long ramInBytes = parseLongRam(output);
                return ramInBytes / 1048576L;
            }
            if (os.contains("Mac")) {
                final String output = executeCommand("sysctl -n hw.memsize");
                final long ramInBytes = parseLongRam(output);
                return ramInBytes / 1048576L;
            }
            Agent.LOG.log(Level.FINER, MessageFormat.format("Could not get total physical memory for OS {0}", os));
        }
        return 0L;
    }
    
    protected static String findMatchInFile(final File file, final Pattern lookFor) {
        if (file.exists() && file.canRead()) {
            BufferedReader reader = null;
            try {
                final FileInputStream fileInputStream = new FileInputStream(file);
                final InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                reader = new BufferedReader(inputStreamReader);
                final Matcher matcher = lookFor.matcher("");
                String line;
                while ((line = reader.readLine()) != null) {
                    matcher.reset(line);
                    if (matcher.find()) {
                        final String group = matcher.group(1);
                        try {
                            if (reader != null) {
                                reader.close();
                            }
                        }
                        catch (IOException ex) {}
                        return group;
                    }
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (IOException ex2) {}
            }
            catch (FileNotFoundException e) {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (IOException ex3) {}
            }
            catch (IOException e2) {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (IOException ex4) {}
            }
            finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (IOException ex5) {}
            }
        }
        else {
            Agent.LOG.log(Level.FINER, MessageFormat.format("Could not read file {0}", file.getName()));
        }
        return null;
    }
    
    protected static long parseLongRam(final String number) {
        try {
            return Long.parseLong(number);
        }
        catch (NumberFormatException e) {
            Agent.LOG.log(Level.FINE, MessageFormat.format("Unable to parse total memory available. Found {0}", number));
            return 0L;
        }
    }
    
    protected static String executeCommand(final String command) {
        final StringBuffer output = new StringBuffer();
        try {
            MemoryData.process = Runtime.getRuntime().exec(command);
            final BufferedReader procOutput = new BufferedReader(new InputStreamReader(MemoryData.process.getInputStream()));
            String line;
            while ((line = procOutput.readLine()) != null) {
                output.append(line);
            }
            MemoryData.process.waitFor();
            if (MemoryData.process != null) {
                MemoryData.process.destroy();
            }
        }
        catch (IOException e) {
            Agent.LOG.log(Level.FINEST, MessageFormat.format("An exception ocurred {0}", e));
            if (MemoryData.process != null) {
                MemoryData.process.destroy();
            }
        }
        catch (InterruptedException e2) {
            Agent.LOG.log(Level.FINER, "Memory utilization task was interrupted.");
            if (MemoryData.process != null) {
                MemoryData.process.destroy();
            }
        }
        finally {
            if (MemoryData.process != null) {
                MemoryData.process.destroy();
            }
        }
        return output.toString();
    }
    
    public static void cleanup() {
        if (MemoryData.process != null) {
            MemoryData.process.destroy();
        }
    }
    
    static {
        LINUX_MEMORY_PATTERN = Pattern.compile("MemTotal: \\s+(\\d+)\\skB");
        MemoryData.process = null;
    }
}
