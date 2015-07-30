// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling;

import java.io.File;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.AsynchronousCompressor;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.FileFilterUtil;
import java.util.Date;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.CompressionMode;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.ArchiveRemover;
import java.util.concurrent.Future;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.RenameUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.Compressor;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.FileNamePattern;

public class TimeBasedRollingPolicy<E> extends RollingPolicyBase implements TriggeringPolicy<E>
{
    static final String FNP_NOT_SET = "The FileNamePattern option must be set before using TimeBasedRollingPolicy. ";
    static final int INFINITE_HISTORY = 0;
    FileNamePattern fileNamePatternWCS;
    private Compressor compressor;
    private RenameUtil renameUtil;
    Future<?> future;
    private int maxHistory;
    private ArchiveRemover archiveRemover;
    TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedFileNamingAndTriggeringPolicy;
    boolean cleanHistoryOnStart;
    
    public TimeBasedRollingPolicy() {
        this.renameUtil = new RenameUtil();
        this.maxHistory = 0;
        this.cleanHistoryOnStart = false;
    }
    
    public void start() {
        this.renameUtil.setContext(this.context);
        if (this.fileNamePatternStr != null) {
            this.fileNamePattern = new FileNamePattern(this.fileNamePatternStr, this.context);
            this.determineCompressionMode();
            (this.compressor = new Compressor(this.compressionMode)).setContext(this.context);
            this.fileNamePatternWCS = new FileNamePattern(Compressor.computeFileNameStr_WCS(this.fileNamePatternStr, this.compressionMode), this.context);
            this.addInfo("Will use the pattern " + this.fileNamePatternWCS + " for the active file");
            if (this.compressionMode == CompressionMode.ZIP) {
                final String zipEntryFileNamePatternStr = this.transformFileNamePattern2ZipEntry(this.fileNamePatternStr);
                this.zipEntryFileNamePattern = new FileNamePattern(zipEntryFileNamePatternStr, this.context);
            }
            if (this.timeBasedFileNamingAndTriggeringPolicy == null) {
                this.timeBasedFileNamingAndTriggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy<E>();
            }
            this.timeBasedFileNamingAndTriggeringPolicy.setContext(this.context);
            this.timeBasedFileNamingAndTriggeringPolicy.setTimeBasedRollingPolicy(this);
            this.timeBasedFileNamingAndTriggeringPolicy.start();
            if (this.maxHistory != 0) {
                (this.archiveRemover = this.timeBasedFileNamingAndTriggeringPolicy.getArchiveRemover()).setMaxHistory(this.maxHistory);
                if (this.cleanHistoryOnStart) {
                    this.addInfo("Cleaning on start up");
                    this.archiveRemover.clean(new Date(this.timeBasedFileNamingAndTriggeringPolicy.getCurrentTime()));
                }
            }
            super.start();
            return;
        }
        this.addWarn("The FileNamePattern option must be set before using TimeBasedRollingPolicy. ");
        this.addWarn("See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
        throw new IllegalStateException("The FileNamePattern option must be set before using TimeBasedRollingPolicy. See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
    }
    
    private String transformFileNamePattern2ZipEntry(final String fileNamePatternStr) {
        final String slashified = FileFilterUtil.slashify(fileNamePatternStr);
        return FileFilterUtil.afterLastSlash(slashified);
    }
    
    public void setTimeBasedFileNamingAndTriggeringPolicy(final TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedTriggering) {
        this.timeBasedFileNamingAndTriggeringPolicy = timeBasedTriggering;
    }
    
    public TimeBasedFileNamingAndTriggeringPolicy<E> getTimeBasedFileNamingAndTriggeringPolicy() {
        return this.timeBasedFileNamingAndTriggeringPolicy;
    }
    
    public void rollover() throws RolloverFailure {
        final String elapsedPeriodsFileName = this.timeBasedFileNamingAndTriggeringPolicy.getElapsedPeriodsFileName();
        final String elpasedPeriodStem = FileFilterUtil.afterLastSlash(elapsedPeriodsFileName);
        if (this.compressionMode == CompressionMode.NONE) {
            if (this.getParentsRawFileProperty() != null) {
                this.renameUtil.rename(this.getParentsRawFileProperty(), elapsedPeriodsFileName);
            }
        }
        else if (this.getParentsRawFileProperty() == null) {
            this.future = (Future<?>)this.asyncCompress(elapsedPeriodsFileName, elapsedPeriodsFileName, elpasedPeriodStem);
        }
        else {
            this.future = (Future<?>)this.renamedRawAndAsyncCompress(elapsedPeriodsFileName, elpasedPeriodStem);
        }
        if (this.archiveRemover != null) {
            this.archiveRemover.clean(new Date(this.timeBasedFileNamingAndTriggeringPolicy.getCurrentTime()));
        }
    }
    
    Future asyncCompress(final String nameOfFile2Compress, final String nameOfCompressedFile, final String innerEntryName) throws RolloverFailure {
        final AsynchronousCompressor ac = new AsynchronousCompressor(this.compressor);
        return ac.compressAsynchronously(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
    }
    
    Future renamedRawAndAsyncCompress(final String nameOfCompressedFile, final String innerEntryName) throws RolloverFailure {
        final String parentsRawFile = this.getParentsRawFileProperty();
        final String tmpTarget = parentsRawFile + System.nanoTime() + ".tmp";
        this.renameUtil.rename(parentsRawFile, tmpTarget);
        return this.asyncCompress(tmpTarget, nameOfCompressedFile, innerEntryName);
    }
    
    public String getActiveFileName() {
        final String parentsRawFileProperty = this.getParentsRawFileProperty();
        if (parentsRawFileProperty != null) {
            return parentsRawFileProperty;
        }
        return this.timeBasedFileNamingAndTriggeringPolicy.getCurrentPeriodsFileNameWithoutCompressionSuffix();
    }
    
    public boolean isTriggeringEvent(final File activeFile, final E event) {
        return this.timeBasedFileNamingAndTriggeringPolicy.isTriggeringEvent(activeFile, event);
    }
    
    public int getMaxHistory() {
        return this.maxHistory;
    }
    
    public void setMaxHistory(final int maxHistory) {
        this.maxHistory = maxHistory;
    }
    
    public boolean isCleanHistoryOnStart() {
        return this.cleanHistoryOnStart;
    }
    
    public void setCleanHistoryOnStart(final boolean cleanHistoryOnStart) {
        this.cleanHistoryOnStart = cleanHistoryOnStart;
    }
    
    public String toString() {
        return "c.q.l.core.rolling.TimeBasedRollingPolicy";
    }
}
