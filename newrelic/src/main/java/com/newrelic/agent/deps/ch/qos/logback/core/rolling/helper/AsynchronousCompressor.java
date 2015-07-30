// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsynchronousCompressor
{
    Compressor compressor;
    
    public AsynchronousCompressor(final Compressor compressor) {
        this.compressor = compressor;
    }
    
    public Future<?> compressAsynchronously(final String nameOfFile2Compress, final String nameOfCompressedFile, final String innerEntryName) {
        final ExecutorService executor = Executors.newScheduledThreadPool(1);
        final Future<?> future = executor.submit(new CompressionRunnable(this.compressor, nameOfFile2Compress, nameOfCompressedFile, innerEntryName));
        executor.shutdown();
        return future;
    }
}
