// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.util.Date;

public class SizeAndTimeBasedArchiveRemover extends DefaultArchiveRemover
{
    public SizeAndTimeBasedArchiveRemover(final FileNamePattern fileNamePattern, final RollingCalendar rc) {
        super(fileNamePattern, rc);
    }
    
    public void cleanByPeriodOffset(final Date now, final int periodOffset) {
        final Date dateOfPeriodToClean = this.rc.getRelativeDate(now, periodOffset);
        final String regex = this.fileNamePattern.toRegex(dateOfPeriodToClean);
        final String stemRegex = FileFilterUtil.afterLastSlash(regex);
        File archive0 = new File(this.fileNamePattern.convertMultipleArguments(dateOfPeriodToClean, 0));
        archive0 = archive0.getAbsoluteFile();
        final File parentDir = archive0.getAbsoluteFile().getParentFile();
        final File[] arr$;
        final File[] matchingFileArray = arr$ = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
        for (final File f : arr$) {
            f.delete();
        }
        if (this.parentClean) {
            this.removeFolderIfEmpty(parentDir);
        }
    }
}
