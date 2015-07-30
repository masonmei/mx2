// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import com.newrelic.agent.deps.ch.qos.logback.core.util.FileUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.RolloverFailure;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.RollingFileAppender;
import java.io.File;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class RenameUtil extends ContextAwareBase
{
    static String RENAMING_ERROR_URL;
    static final int BUF_SIZE = 32768;
    
    public void rename(final String from, final String to) throws RolloverFailure {
        if (from.equals(to)) {
            this.addWarn("From and to file are the same [" + from + "]. Skipping.");
            return;
        }
        final File fromFile = new File(from);
        if (fromFile.exists()) {
            final File toFile = new File(to);
            this.createMissingTargetDirsIfNecessary(toFile);
            this.addInfo("Renaming file [" + fromFile + "] to [" + toFile + "]");
            final boolean result = fromFile.renameTo(toFile);
            if (!result) {
                this.addWarn("Failed to rename file [" + fromFile + "] to [" + toFile + "].");
                this.addWarn("Please consider leaving the [file] option of " + RollingFileAppender.class.getSimpleName() + " empty.");
                this.addWarn("See also " + RenameUtil.RENAMING_ERROR_URL);
            }
            return;
        }
        throw new RolloverFailure("File [" + from + "] does not exist.");
    }
    
    public void renameByCopying(final String from, final String to) throws RolloverFailure {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(from));
            bos = new BufferedOutputStream(new FileOutputStream(to));
            final byte[] inbuf = new byte[32768];
            int n;
            while ((n = bis.read(inbuf)) != -1) {
                bos.write(inbuf, 0, n);
            }
            bis.close();
            bis = null;
            bos.close();
            bos = null;
            final File fromFile = new File(from);
            if (!fromFile.delete()) {
                this.addWarn("Could not delete " + from);
            }
        }
        catch (IOException ioe) {
            this.addError("Failed to rename file by copying", ioe);
            throw new RolloverFailure("Failed to rename file by copying");
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException ex) {}
            }
            if (bos != null) {
                try {
                    bos.close();
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    void createMissingTargetDirsIfNecessary(final File toFile) throws RolloverFailure {
        if (FileUtil.isParentDirectoryCreationRequired(toFile)) {
            final boolean result = FileUtil.createMissingParentDirectories(toFile);
            if (!result) {
                throw new RolloverFailure("Failed to create parent directories for [" + toFile.getAbsolutePath() + "]");
            }
        }
    }
    
    public String toString() {
        return "c.q.l.co.rolling.helper.RenameUtil";
    }
    
    static {
        RenameUtil.RENAMING_ERROR_URL = "http://logback.qos.ch/codes.html#renamingError";
    }
}
