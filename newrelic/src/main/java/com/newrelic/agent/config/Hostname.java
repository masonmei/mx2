// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.net.Inet6Address;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.util.Iterator;
import java.net.SocketException;
import java.util.Collections;
import java.net.NetworkInterface;
import java.util.List;
import java.lang.management.ManagementFactory;
import com.newrelic.agent.Agent;
import java.util.logging.Level;
import com.newrelic.agent.logging.IAgentLogger;
import java.net.UnknownHostException;
import java.net.InetAddress;

public class Hostname
{
    public static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            return "localhost";
        }
    }
    
    public static String getDisplayHostname(final IAgentLogger log, final AgentConfig config, final String defaultHostname, final String appName) {
        final String specifiedHost = (String)config.getValue("process_host.display_name", (Object)defaultHostname);
        log.log(Level.INFO, "Display host name is {0} for application {1}", new Object[] { specifiedHost, appName });
        return specifiedHost;
    }
    
    public static String getHostname(final IAgentLogger log, final AgentConfig config) {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            Agent.LOG.log(Level.FINE, "Error getting host name", e);
            try {
                final InetAddress inetAddress = getInetAddress(config);
                if (inetAddress == null) {
                    Agent.LOG.severe("Unable to obtain a host name for this JVM, defaulting to localhost." + getMessage());
                    return "localhost";
                }
                Agent.LOG.severe("Unable to obtain a host name for this JVM.  Using IP address." + getMessage());
                return inetAddress.getHostAddress();
            }
            catch (Exception err) {
                Agent.LOG.log(Level.FINE, "Error getting IP address", err);
                return "localhost";
            }
        }
    }
    
    private static String getMessage() {
        final String osName = ManagementFactory.getOperatingSystemMXBean().getName();
        if ("Linux".equals(osName) || "Mac OS X".equals(osName)) {
            return "  You might need to add a host entry for this machine in /etc/hosts";
        }
        return "";
    }
    
    private static List<NetworkInterface> getNetworkInterfaces() {
        try {
            return Collections.list(NetworkInterface.getNetworkInterfaces());
        }
        catch (SocketException e) {
            return Collections.emptyList();
        }
    }
    
    protected static InetAddress getInetAddress(final AgentConfig config) {
        final List<NetworkInterface> networkInterfaces = getNetworkInterfaces();
        if (!networkInterfaces.isEmpty()) {
            final Boolean isIpv4 = preferIpv4(config);
            for (final NetworkInterface networkInterface : networkInterfaces) {
                if (networkInterface.getName().startsWith("eth") || networkInterface.getName().startsWith("br") || networkInterface.getName().startsWith("wl") || networkInterface.getName().startsWith("en")) {
                    final InetAddress inetAddress = getInetAddress(networkInterface, isIpv4);
                    if (inetAddress != null) {
                        return inetAddress;
                    }
                    continue;
                }
            }
        }
        return null;
    }
    
    protected static Boolean preferIpv4(final AgentConfig config) {
        final Object value = config.getValue("process_host.ipv_preference", (Object)null);
        if (value != null) {
            if ("6".equals(String.valueOf(value))) {
                return Boolean.FALSE;
            }
            if ("4".equals(String.valueOf(value))) {
                return Boolean.TRUE;
            }
        }
        return null;
    }
    
    private static InetAddress getInetAddress(final NetworkInterface networkInterface, final Boolean isIpv4) {
        final List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
        if (interfaceAddresses == null) {
            return null;
        }
        InetAddress candidate = null;
        for (final InterfaceAddress interfaceAddress : interfaceAddresses) {
            final InetAddress inetAddress = interfaceAddress.getAddress();
            if (inetAddress != null) {
                if (isIpv4 == null) {
                    candidate = inetAddress;
                    break;
                }
                if (inetAddress instanceof Inet4Address) {
                    candidate = inetAddress;
                    if (isIpv4) {
                        break;
                    }
                }
                if (!(inetAddress instanceof Inet6Address)) {
                    continue;
                }
                candidate = inetAddress;
                if (!isIpv4) {
                    break;
                }
                continue;
            }
        }
        return candidate;
    }
}
