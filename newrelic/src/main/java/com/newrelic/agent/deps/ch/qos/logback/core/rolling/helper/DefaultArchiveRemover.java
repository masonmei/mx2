// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import java.io.File;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.Converter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.LiteralConverter;
import java.util.Date;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public abstract class DefaultArchiveRemover extends ContextAwareBase implements ArchiveRemover
{
    protected static final long UNINITIALIZED = -1L;
    protected static final long INACTIVITY_TOLERANCE_IN_MILLIS = 5529600000L;
    static final int MAX_VALUE_FOR_INACTIVITY_PERIODS = 336;
    final FileNamePattern fileNamePattern;
    final RollingCalendar rc;
    int periodOffsetForDeletionTarget;
    final boolean parentClean;
    long lastHeartBeat;
    
    public DefaultArchiveRemover(final FileNamePattern fileNamePattern, final RollingCalendar rc) {
        this.lastHeartBeat = -1L;
        this.fileNamePattern = fileNamePattern;
        this.rc = rc;
        this.parentClean = this.computeParentCleaningFlag(fileNamePattern);
    }
    
    int computeElapsedPeriodsSinceLastClean(final long nowInMillis) {
        long periodsElapsed = 0L;
        if (this.lastHeartBeat == -1L) {
            this.addInfo("first clean up after appender initialization");
            periodsElapsed = this.rc.periodsElapsed(nowInMillis, nowInMillis + 5529600000L);
            if (periodsElapsed > 336L) {
                periodsElapsed = 336L;
            }
        }
        else {
            periodsElapsed = this.rc.periodsElapsed(this.lastHeartBeat, nowInMillis);
            if (periodsElapsed < 1L) {
                this.addWarn("Unexpected periodsElapsed value " + periodsElapsed);
                periodsElapsed = 1L;
            }
        }
        return (int)periodsElapsed;
    }
    
    public void clean(final Date now) {
        final long nowInMillis = now.getTime();
        final int periodsElapsed = this.computeElapsedPeriodsSinceLastClean(nowInMillis);
        this.lastHeartBeat = nowInMillis;
        if (periodsElapsed > 1) {
            this.addInfo("periodsElapsed = " + periodsElapsed);
        }
        for (int i = 0; i < periodsElapsed; ++i) {
            this.cleanByPeriodOffset(now, this.periodOffsetForDeletionTarget - i);
        }
    }
    
    abstract void cleanByPeriodOffset(final Date p0, final int p1);
    
    boolean computeParentCleaningFlag(final FileNamePattern fileNamePattern) {
        final DateTokenConverter dtc = fileNamePattern.getPrimaryDateTokenConverter();
        if (dtc.getDatePattern().indexOf(47) != -1) {
            return true;
        }
        Converter<Object> p;
        for (p = fileNamePattern.headTokenConverter; p != null; p = p.getNext()) {
            if (p instanceof DateTokenConverter) {
                break;
            }
        }
        while (p != null) {
            if (p instanceof LiteralConverter) {
                final String s = p.convert(null);
                if (s.indexOf(47) != -1) {
                    return true;
                }
            }
            p = p.getNext();
        }
        return false;
    }
    
    void removeFolderIfEmpty(final File dir) {
        this.removeFolderIfEmpty(dir, 0);
    }
    
    private void removeFolderIfEmpty(final File dir, final int depth) {
        if (depth >= 3) {
            return;
        }
        if (dir.isDirectory() && FileFilterUtil.isEmptyDirectory(dir)) {
            this.addInfo("deleting folder [" + dir + "]");
            dir.delete();
            this.removeFolderIfEmpty(dir.getParentFile(), depth + 1);
        }
    }
    
    public void setMaxHistory(final int maxHistory) {
        this.periodOffsetForDeletionTarget = -maxHistory - 1;
    }
}
