// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling;

import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.DateTokenConverter;
import java.io.File;
import java.util.Date;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.RollingCalendar;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.ArchiveRemover;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public abstract class TimeBasedFileNamingAndTriggeringPolicyBase<E> extends ContextAwareBase implements TimeBasedFileNamingAndTriggeringPolicy<E>
{
    protected TimeBasedRollingPolicy<E> tbrp;
    protected ArchiveRemover archiveRemover;
    protected String elapsedPeriodsFileName;
    protected RollingCalendar rc;
    protected long artificialCurrentTime;
    protected Date dateInCurrentPeriod;
    protected long nextCheck;
    protected boolean started;
    
    public TimeBasedFileNamingAndTriggeringPolicyBase() {
        this.archiveRemover = null;
        this.artificialCurrentTime = -1L;
        this.dateInCurrentPeriod = null;
        this.started = false;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public void start() {
        final DateTokenConverter dtc = this.tbrp.fileNamePattern.getPrimaryDateTokenConverter();
        if (dtc == null) {
            throw new IllegalStateException("FileNamePattern [" + this.tbrp.fileNamePattern.getPattern() + "] does not contain a valid DateToken");
        }
        (this.rc = new RollingCalendar()).init(dtc.getDatePattern());
        this.addInfo("The date pattern is '" + dtc.getDatePattern() + "' from file name pattern '" + this.tbrp.fileNamePattern.getPattern() + "'.");
        this.rc.printPeriodicity(this);
        this.setDateInCurrentPeriod(new Date(this.getCurrentTime()));
        if (this.tbrp.getParentsRawFileProperty() != null) {
            final File currentFile = new File(this.tbrp.getParentsRawFileProperty());
            if (currentFile.exists() && currentFile.canRead()) {
                this.setDateInCurrentPeriod(new Date(currentFile.lastModified()));
            }
        }
        this.addInfo("Setting initial period to " + this.dateInCurrentPeriod);
        this.computeNextCheck();
    }
    
    public void stop() {
        this.started = false;
    }
    
    protected void computeNextCheck() {
        this.nextCheck = this.rc.getNextTriggeringMillis(this.dateInCurrentPeriod);
    }
    
    protected void setDateInCurrentPeriod(final long now) {
        this.dateInCurrentPeriod.setTime(now);
    }
    
    public void setDateInCurrentPeriod(final Date _dateInCurrentPeriod) {
        this.dateInCurrentPeriod = _dateInCurrentPeriod;
    }
    
    public String getElapsedPeriodsFileName() {
        return this.elapsedPeriodsFileName;
    }
    
    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return this.tbrp.fileNamePatternWCS.convert(this.dateInCurrentPeriod);
    }
    
    public void setCurrentTime(final long timeInMillis) {
        this.artificialCurrentTime = timeInMillis;
    }
    
    public long getCurrentTime() {
        if (this.artificialCurrentTime >= 0L) {
            return this.artificialCurrentTime;
        }
        return System.currentTimeMillis();
    }
    
    public void setTimeBasedRollingPolicy(final TimeBasedRollingPolicy<E> _tbrp) {
        this.tbrp = _tbrp;
    }
    
    public ArchiveRemover getArchiveRemover() {
        return this.archiveRemover;
    }
}
