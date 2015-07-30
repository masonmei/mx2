// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.util;

import com.newrelic.agent.deps.ch.qos.logback.core.status.WarnStatus;
import com.newrelic.agent.deps.ch.qos.logback.core.status.InfoStatus;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import java.net.URL;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;

public class ConfigurationWatchListUtil
{
    static final ConfigurationWatchListUtil origin;
    
    public static void setMainWatchURL(final Context context, final URL url) {
        ConfigurationWatchList cwl = getConfigurationWatchList(context);
        if (cwl == null) {
            cwl = new ConfigurationWatchList();
            cwl.setContext(context);
            context.putObject("CONFIGURATION_WATCH_LIST", cwl);
        }
        else {
            cwl.clear();
        }
        setConfigurationWatchListResetFlag(context, true);
        cwl.setMainURL(url);
    }
    
    public static URL getMainWatchURL(final Context context) {
        final ConfigurationWatchList cwl = getConfigurationWatchList(context);
        if (cwl == null) {
            return null;
        }
        return cwl.getMainURL();
    }
    
    public static void addToWatchList(final Context context, final URL url) {
        final ConfigurationWatchList cwl = getConfigurationWatchList(context);
        if (cwl == null) {
            addWarn(context, "Null ConfigurationWatchList. Cannot add " + url);
        }
        else {
            addInfo(context, "Adding [" + url + "] to configuration watch list.");
            cwl.addToWatchList(url);
        }
    }
    
    public static boolean wasConfigurationWatchListReset(final Context context) {
        final Object o = context.getObject("CONFIGURATION_WATCH_LIST_RESET");
        return o != null && (boolean)o;
    }
    
    public static void setConfigurationWatchListResetFlag(final Context context, final boolean val) {
        context.putObject("CONFIGURATION_WATCH_LIST_RESET", new Boolean(val));
    }
    
    public static ConfigurationWatchList getConfigurationWatchList(final Context context) {
        return (ConfigurationWatchList)context.getObject("CONFIGURATION_WATCH_LIST");
    }
    
    static void addStatus(final Context context, final Status s) {
        if (context == null) {
            System.out.println("Null context in " + ConfigurationWatchList.class.getName());
            return;
        }
        final StatusManager sm = context.getStatusManager();
        if (sm == null) {
            return;
        }
        sm.add(s);
    }
    
    static void addInfo(final Context context, final String msg) {
        addStatus(context, new InfoStatus(msg, ConfigurationWatchListUtil.origin));
    }
    
    static void addWarn(final Context context, final String msg) {
        addStatus(context, new WarnStatus(msg, ConfigurationWatchListUtil.origin));
    }
    
    static {
        origin = new ConfigurationWatchListUtil();
    }
}
