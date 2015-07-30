// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;

public class StatusChecker
{
    StatusManager sm;
    
    public StatusChecker(final StatusManager sm) {
        this.sm = sm;
    }
    
    public StatusChecker(final Context context) {
        this.sm = context.getStatusManager();
    }
    
    public boolean hasXMLParsingErrors(final long threshold) {
        return this.containsMatch(threshold, 2, "XML_PARSING");
    }
    
    public boolean noXMLParsingErrorsOccurred(final long threshold) {
        return !this.hasXMLParsingErrors(threshold);
    }
    
    public int getHighestLevel(final long threshold) {
        final List<Status> filteredList = StatusUtil.filterStatusListByTimeThreshold(this.sm.getCopyOfStatusList(), threshold);
        int maxLevel = 0;
        for (final Status s : filteredList) {
            if (s.getLevel() > maxLevel) {
                maxLevel = s.getLevel();
            }
        }
        return maxLevel;
    }
    
    public boolean isErrorFree(final long threshold) {
        return 2 > this.getHighestLevel(threshold);
    }
    
    public boolean containsMatch(final long threshold, final int level, final String regex) {
        final List<Status> filteredList = StatusUtil.filterStatusListByTimeThreshold(this.sm.getCopyOfStatusList(), threshold);
        final Pattern p = Pattern.compile(regex);
        for (final Status status : filteredList) {
            if (level != status.getLevel()) {
                continue;
            }
            final String msg = status.getMessage();
            final Matcher matcher = p.matcher(msg);
            if (matcher.lookingAt()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsMatch(final int level, final String regex) {
        return this.containsMatch(0L, level, regex);
    }
    
    public boolean containsMatch(final String regex) {
        final Pattern p = Pattern.compile(regex);
        for (final Status status : this.sm.getCopyOfStatusList()) {
            final String msg = status.getMessage();
            final Matcher matcher = p.matcher(msg);
            if (matcher.lookingAt()) {
                return true;
            }
        }
        return false;
    }
    
    public int matchCount(final String regex) {
        int count = 0;
        final Pattern p = Pattern.compile(regex);
        for (final Status status : this.sm.getCopyOfStatusList()) {
            final String msg = status.getMessage();
            final Matcher matcher = p.matcher(msg);
            if (matcher.lookingAt()) {
                ++count;
            }
        }
        return count;
    }
    
    public boolean containsException(final Class exceptionType) {
        for (final Status status : this.sm.getCopyOfStatusList()) {
            final Throwable t = status.getThrowable();
            if (t != null && t.getClass().getName().equals(exceptionType.getName())) {
                return true;
            }
        }
        return false;
    }
    
    public long timeOfLastReset() {
        final List<Status> statusList = this.sm.getCopyOfStatusList();
        if (statusList == null) {
            return -1L;
        }
        final int len = statusList.size();
        for (int i = len - 1; i >= 0; --i) {
            final Status s = statusList.get(i);
            if ("Will reset and reconfigure context ".equals(s.getMessage())) {
                return s.getDate();
            }
        }
        return -1L;
    }
}
