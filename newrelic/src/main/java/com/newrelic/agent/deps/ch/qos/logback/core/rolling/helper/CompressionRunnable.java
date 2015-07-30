// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

public class CompressionRunnable implements Runnable
{
    final Compressor compressor;
    final String nameOfFile2Compress;
    final String nameOfCompressedFile;
    final String innerEntryName;
    
    public CompressionRunnable(final Compressor compressor, final String nameOfFile2Compress, final String nameOfCompressedFile, final String innerEntryName) {
        this.compressor = compressor;
        this.nameOfFile2Compress = nameOfFile2Compress;
        this.nameOfCompressedFile = nameOfCompressedFile;
        this.innerEntryName = innerEntryName;
    }
    
    public void run() {
        this.compressor.compress(this.nameOfFile2Compress, this.nameOfCompressedFile, this.innerEntryName);
    }
}
