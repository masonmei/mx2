// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import com.newrelic.agent.deps.ch.qos.logback.core.util.FileUtil;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.io.IOException;
import com.newrelic.agent.deps.ch.qos.logback.core.status.ErrorStatus;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.WarnStatus;
import java.io.File;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class Compressor extends ContextAwareBase
{
    final CompressionMode compressionMode;
    static final int BUFFER_SIZE = 8192;
    
    public Compressor(final CompressionMode compressionMode) {
        this.compressionMode = compressionMode;
    }
    
    public void compress(final String nameOfFile2Compress, final String nameOfCompressedFile, final String innerEntryName) {
        switch (this.compressionMode) {
            case GZ: {
                this.gzCompress(nameOfFile2Compress, nameOfCompressedFile);
                break;
            }
            case ZIP: {
                this.zipCompress(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
                break;
            }
            case NONE: {
                throw new UnsupportedOperationException("compress method called in NONE compression mode");
            }
        }
    }
    
    private void zipCompress(final String nameOfFile2zip, String nameOfZippedFile, final String innerEntryName) {
        final File file2zip = new File(nameOfFile2zip);
        if (!file2zip.exists()) {
            this.addStatus(new WarnStatus("The file to compress named [" + nameOfFile2zip + "] does not exist.", this));
            return;
        }
        if (innerEntryName == null) {
            this.addStatus(new WarnStatus("The innerEntryName parameter cannot be null", this));
            return;
        }
        if (!nameOfZippedFile.endsWith(".zip")) {
            nameOfZippedFile += ".zip";
        }
        final File zippedFile = new File(nameOfZippedFile);
        if (zippedFile.exists()) {
            this.addStatus(new WarnStatus("The target compressed file named [" + nameOfZippedFile + "] exist already.", this));
            return;
        }
        this.addInfo("ZIP compressing [" + file2zip + "] as [" + zippedFile + "]");
        this.createMissingTargetDirsIfNecessary(zippedFile);
        BufferedInputStream bis = null;
        ZipOutputStream zos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(nameOfFile2zip));
            zos = new ZipOutputStream(new FileOutputStream(nameOfZippedFile));
            final ZipEntry zipEntry = this.computeZipEntry(innerEntryName);
            zos.putNextEntry(zipEntry);
            final byte[] inbuf = new byte[8192];
            int n;
            while ((n = bis.read(inbuf)) != -1) {
                zos.write(inbuf, 0, n);
            }
            bis.close();
            bis = null;
            zos.close();
            zos = null;
            if (!file2zip.delete()) {
                this.addStatus(new WarnStatus("Could not delete [" + nameOfFile2zip + "].", this));
            }
        }
        catch (Exception e) {
            this.addStatus(new ErrorStatus("Error occurred while compressing [" + nameOfFile2zip + "] into [" + nameOfZippedFile + "].", this, e));
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException ex) {}
            }
            if (zos != null) {
                try {
                    zos.close();
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    ZipEntry computeZipEntry(final File zippedFile) {
        return this.computeZipEntry(zippedFile.getName());
    }
    
    ZipEntry computeZipEntry(final String filename) {
        final String nameOfFileNestedWithinArchive = computeFileNameStr_WCS(filename, this.compressionMode);
        return new ZipEntry(nameOfFileNestedWithinArchive);
    }
    
    private void gzCompress(final String nameOfFile2gz, String nameOfgzedFile) {
        final File file2gz = new File(nameOfFile2gz);
        if (!file2gz.exists()) {
            this.addStatus(new WarnStatus("The file to compress named [" + nameOfFile2gz + "] does not exist.", this));
            return;
        }
        if (!nameOfgzedFile.endsWith(".gz")) {
            nameOfgzedFile += ".gz";
        }
        final File gzedFile = new File(nameOfgzedFile);
        if (gzedFile.exists()) {
            this.addWarn("The target compressed file named [" + nameOfgzedFile + "] exist already. Aborting file compression.");
            return;
        }
        this.addInfo("GZ compressing [" + file2gz + "] as [" + gzedFile + "]");
        this.createMissingTargetDirsIfNecessary(gzedFile);
        BufferedInputStream bis = null;
        GZIPOutputStream gzos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(nameOfFile2gz));
            gzos = new GZIPOutputStream(new FileOutputStream(nameOfgzedFile));
            final byte[] inbuf = new byte[8192];
            int n;
            while ((n = bis.read(inbuf)) != -1) {
                gzos.write(inbuf, 0, n);
            }
            bis.close();
            bis = null;
            gzos.close();
            gzos = null;
            if (!file2gz.delete()) {
                this.addStatus(new WarnStatus("Could not delete [" + nameOfFile2gz + "].", this));
            }
        }
        catch (Exception e) {
            this.addStatus(new ErrorStatus("Error occurred while compressing [" + nameOfFile2gz + "] into [" + nameOfgzedFile + "].", this, e));
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException ex) {}
            }
            if (gzos != null) {
                try {
                    gzos.close();
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    public static String computeFileNameStr_WCS(final String fileNamePatternStr, final CompressionMode compressionMode) {
        final int len = fileNamePatternStr.length();
        switch (compressionMode) {
            case GZ: {
                if (fileNamePatternStr.endsWith(".gz")) {
                    return fileNamePatternStr.substring(0, len - 3);
                }
                return fileNamePatternStr;
            }
            case ZIP: {
                if (fileNamePatternStr.endsWith(".zip")) {
                    return fileNamePatternStr.substring(0, len - 4);
                }
                return fileNamePatternStr;
            }
            case NONE: {
                return fileNamePatternStr;
            }
            default: {
                throw new IllegalStateException("Execution should not reach this point");
            }
        }
    }
    
    void createMissingTargetDirsIfNecessary(final File file) {
        if (FileUtil.isParentDirectoryCreationRequired(file)) {
            final boolean result = FileUtil.createMissingParentDirectories(file);
            if (!result) {
                this.addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
            }
            else {
                this.addInfo("Created missing parent directories for [" + file.getAbsolutePath() + "]");
            }
        }
    }
    
    public String toString() {
        return this.getClass().getName();
    }
}
