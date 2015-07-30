// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;

public class StatusUtil
{
    public static boolean contextHasStatusListener(final Context context) {
        final StatusManager sm = context.getStatusManager();
        if (sm == null) {
            return false;
        }
        final List<StatusListener> listeners = sm.getCopyOfStatusListenerList();
        return listeners != null && listeners.size() != 0;
    }
    
    public static List<Status> filterStatusListByTimeThreshold(final List<Status> rawList, final long threshold) {
        final List<Status> filteredList = new ArrayList<Status>();
        for (final Status s : rawList) {
            if (s.getDate() >= threshold) {
                filteredList.add(s);
            }
        }
        return filteredList;
    }
    
    public static void addStatus(final Context context, final Status status) {
        if (context == null) {
            return;
        }
        final StatusManager sm = context.getStatusManager();
        if (sm != null) {
            sm.add(status);
        }
    }
    
    public static void addInfo(final Context context, final Object caller, final String msg) {
        addStatus(context, new InfoStatus(msg, caller));
    }
    
    public static void addWarn(final Context context, final Object caller, final String msg) {
        addStatus(context, new WarnStatus(msg, caller));
    }
    
    public static void addError(final Context context, final Object caller, final String msg, final Throwable t) {
        addStatus(context, new ErrorStatus(msg, caller, t));
    }
}
