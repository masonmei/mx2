// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling;

import java.util.Date;
import java.io.File;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover;

public class DefaultTimeBasedFileNamingAndTriggeringPolicy<E> extends TimeBasedFileNamingAndTriggeringPolicyBase<E>
{
    public void start() {
        super.start();
        (this.archiveRemover = new TimeBasedArchiveRemover(this.tbrp.fileNamePattern, this.rc)).setContext(this.context);
        this.started = true;
    }
    
    public boolean isTriggeringEvent(final File activeFile, final E event) {
        final long time = this.getCurrentTime();
        if (time >= this.nextCheck) {
            final Date dateOfElapsedPeriod = this.dateInCurrentPeriod;
            this.addInfo("Elapsed period: " + dateOfElapsedPeriod);
            this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWCS.convert(dateOfElapsedPeriod);
            this.setDateInCurrentPeriod(time);
            this.computeNextCheck();
            return true;
        }
        return false;
    }
    
    public String toString() {
        return "c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy";
    }
}
