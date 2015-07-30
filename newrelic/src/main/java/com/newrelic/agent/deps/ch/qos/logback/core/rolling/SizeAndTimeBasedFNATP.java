// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling;

import java.util.Date;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.CompressionMode;
import java.io.File;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.FileFilterUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.SizeAndTimeBasedArchiveRemover;
import com.newrelic.agent.deps.ch.qos.logback.core.util.FileSize;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.NoAutoStart;

@NoAutoStart
public class SizeAndTimeBasedFNATP<E> extends TimeBasedFileNamingAndTriggeringPolicyBase<E>
{
    int currentPeriodsCounter;
    FileSize maxFileSize;
    String maxFileSizeAsString;
    private int invocationCounter;
    private int invocationMask;
    
    public SizeAndTimeBasedFNATP() {
        this.currentPeriodsCounter = 0;
        this.invocationMask = 1;
    }
    
    public void start() {
        super.start();
        (this.archiveRemover = new SizeAndTimeBasedArchiveRemover(this.tbrp.fileNamePattern, this.rc)).setContext(this.context);
        final String regex = this.tbrp.fileNamePattern.toRegex(this.dateInCurrentPeriod);
        final String stemRegex = FileFilterUtil.afterLastSlash(regex);
        this.computeCurrentPeriodsHighestCounterValue(stemRegex);
        this.started = true;
    }
    
    void computeCurrentPeriodsHighestCounterValue(final String stemRegex) {
        final File file = new File(this.getCurrentPeriodsFileNameWithoutCompressionSuffix());
        final File parentDir = file.getParentFile();
        final File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
        if (matchingFileArray == null || matchingFileArray.length == 0) {
            this.currentPeriodsCounter = 0;
            return;
        }
        this.currentPeriodsCounter = FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
        if (this.tbrp.getParentsRawFileProperty() != null || this.tbrp.compressionMode != CompressionMode.NONE) {
            ++this.currentPeriodsCounter;
        }
    }
    
    public boolean isTriggeringEvent(final File activeFile, final E event) {
        final long time = this.getCurrentTime();
        if (time >= this.nextCheck) {
            final Date dateInElapsedPeriod = this.dateInCurrentPeriod;
            this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWCS.convertMultipleArguments(dateInElapsedPeriod, this.currentPeriodsCounter);
            this.currentPeriodsCounter = 0;
            this.setDateInCurrentPeriod(time);
            this.computeNextCheck();
            return true;
        }
        if ((++this.invocationCounter & this.invocationMask) != this.invocationMask) {
            return false;
        }
        if (this.invocationMask < 15) {
            this.invocationMask = (this.invocationMask << 1) + 1;
        }
        if (activeFile.length() >= this.maxFileSize.getSize()) {
            this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWCS.convertMultipleArguments(this.dateInCurrentPeriod, this.currentPeriodsCounter);
            ++this.currentPeriodsCounter;
            return true;
        }
        return false;
    }
    
    private String getFileNameIncludingCompressionSuffix(final Date date, final int counter) {
        return this.tbrp.fileNamePattern.convertMultipleArguments(this.dateInCurrentPeriod, counter);
    }
    
    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return this.tbrp.fileNamePatternWCS.convertMultipleArguments(this.dateInCurrentPeriod, this.currentPeriodsCounter);
    }
    
    public String getMaxFileSize() {
        return this.maxFileSizeAsString;
    }
    
    public void setMaxFileSize(final String maxFileSize) {
        this.maxFileSizeAsString = maxFileSize;
        this.maxFileSize = FileSize.valueOf(maxFileSize);
    }
}
