// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.util.Date;

public class TimeBasedArchiveRemover extends DefaultArchiveRemover
{
    public TimeBasedArchiveRemover(final FileNamePattern fileNamePattern, final RollingCalendar rc) {
        super(fileNamePattern, rc);
    }
    
    protected void cleanByPeriodOffset(final Date now, final int periodOffset) {
        final Date date2delete = this.rc.getRelativeDate(now, periodOffset);
        final String filename = this.fileNamePattern.convert(date2delete);
        final File file2Delete = new File(filename);
        if (file2Delete.exists() && file2Delete.isFile()) {
            file2Delete.delete();
            this.addInfo("deleting " + file2Delete);
            if (this.parentClean) {
                this.removeFolderIfEmpty(file2Delete.getParentFile());
            }
        }
    }
    
    public String toString() {
        return "c.q.l.core.rolling.helper.TimeBasedArchiveRemover";
    }
}
