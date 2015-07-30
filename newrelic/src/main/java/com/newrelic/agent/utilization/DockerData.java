// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.utilization;

import java.util.regex.Matcher;
import com.newrelic.agent.stats.StatsWorks;
import com.newrelic.agent.service.ServiceFactory;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;
import java.io.File;
import java.util.regex.Pattern;

public class DockerData
{
    private static final String FILE_WITH_CONTAIER_ID = "/proc/self/cgroup";
    private static final String CPU = "cpu";
    private static final Pattern DOCKER_NATVIE_DRIVER_WOUT_SYSTEMD;
    private static final Pattern DOCKER_NATIVE_DRIVER_W_SYSTEMD;
    private static final Pattern DOCKER_LXC_DRVIER;
    
    public static String getDockerContainerId(final boolean isLinux) {
        if (isLinux) {
            final File cpuInfoFile = new File("/proc/self/cgroup");
            return getDockerIdFromFile(cpuInfoFile);
        }
        return null;
    }
    
    protected static String getDockerIdFromFile(final File cpuInfoFile) {
        if (cpuInfoFile.exists() && cpuInfoFile.canRead()) {
            try {
                final FileReader fileReader = new FileReader(cpuInfoFile);
                return readFile(fileReader);
            }
            catch (FileNotFoundException ex) {}
        }
        return null;
    }
    
    protected static String readFile(final Reader reader) {
        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(reader);
            final StringBuilder resultGoesHere = new StringBuilder();
            String line;
            while ((line = bReader.readLine()) != null) {
                if (checkLineAndGetResult(line, resultGoesHere)) {
                    final String value = resultGoesHere.toString().trim();
                    if (isInvalidDockerValue(value)) {
                        Agent.LOG.log(Level.WARNING, MessageFormat.format("Failed to validate Docker value {0}", value));
                        recordDockerError();
                        final String s = null;
                        if (bReader != null) {
                            try {
                                bReader.close();
                            }
                            catch (IOException ex) {}
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            }
                            catch (IOException ex2) {}
                        }
                        return s;
                    }
                    final String s2 = value;
                    if (bReader != null) {
                        try {
                            bReader.close();
                        }
                        catch (IOException ex3) {}
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (IOException ex4) {}
                    }
                    return s2;
                }
            }
            if (bReader != null) {
                try {
                    bReader.close();
                }
                catch (IOException ex5) {}
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex6) {}
            }
        }
        catch (Throwable e) {
            Agent.LOG.log(Level.FINEST, e, "Exception occured when reading docker file.", new Object[0]);
            recordDockerError();
            if (bReader != null) {
                try {
                    bReader.close();
                }
                catch (IOException ex7) {}
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex8) {}
            }
        }
        finally {
            if (bReader != null) {
                try {
                    bReader.close();
                }
                catch (IOException ex9) {}
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex10) {}
            }
        }
        return null;
    }
    
    protected static boolean isInvalidDockerValue(final String value) {
        if (value == null) {
            return true;
        }
        if (value.length() != 64) {
            return true;
        }
        for (int i = 0; i < value.length(); ++i) {
            final char c = value.charAt(i);
            if (c < '0' || c > '9') {
                if (c < 'a' || c > 'f') {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected static void recordDockerError() {
        ServiceFactory.getStatsService().doStatsWork(StatsWorks.getIncrementCounterWork("Supportability/utilization/docker/error", 1));
    }
    
    protected static boolean checkLineAndGetResult(final String line, final StringBuilder resultGoesHere) {
        final String[] parts = line.split(":");
        if (parts.length == 3 && validCpuLine(parts[1])) {
            final String mayContainId = parts[2];
            if (checkAndGetMatch(DockerData.DOCKER_NATVIE_DRIVER_WOUT_SYSTEMD, resultGoesHere, mayContainId)) {
                return true;
            }
            if (checkAndGetMatch(DockerData.DOCKER_NATIVE_DRIVER_W_SYSTEMD, resultGoesHere, mayContainId)) {
                return true;
            }
            if (checkAndGetMatch(DockerData.DOCKER_LXC_DRVIER, resultGoesHere, mayContainId)) {
                return true;
            }
            if (!mayContainId.equals("/")) {
                Agent.LOG.log(Level.FINE, "Docker Data: Ignoring unrecognized cgroup ID format: {0}", new Object[] { mayContainId });
            }
        }
        return false;
    }
    
    private static boolean validCpuLine(final String segment) {
        if (segment != null) {
            final String[] arr$;
            final String[] parts = arr$ = segment.split(",");
            for (final String current : arr$) {
                if (current.equals("cpu")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean checkAndGetMatch(final Pattern p, final StringBuilder result, final String segment) {
        final Matcher m = p.matcher(segment);
        if (m.matches() && m.groupCount() == 1) {
            result.append(m.group(1));
            return true;
        }
        return false;
    }
    
    static {
        DOCKER_NATVIE_DRIVER_WOUT_SYSTEMD = Pattern.compile("^/docker/([0-9a-f]+)$");
        DOCKER_NATIVE_DRIVER_W_SYSTEMD = Pattern.compile("^/system\\.slice/docker-([0-9a-f]+)\\.scope$");
        DOCKER_LXC_DRVIER = Pattern.compile("^/lxc/([0-9a-f]+)$");
    }
}
