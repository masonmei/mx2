// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.io;

import java.io.IOException;
import java.util.logging.Level;
import java.io.Flushable;
import java.util.logging.Logger;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public final class Flushables
{
    private static final Logger logger;
    
    public static void flush(final Flushable flushable, final boolean swallowIOException) throws IOException {
        try {
            flushable.flush();
        }
        catch (IOException e) {
            if (!swallowIOException) {
                throw e;
            }
            Flushables.logger.log(Level.WARNING, "IOException thrown while flushing Flushable.", e);
        }
    }
    
    public static void flushQuietly(final Flushable flushable) {
        try {
            flush(flushable, true);
        }
        catch (IOException e) {
            Flushables.logger.log(Level.SEVERE, "IOException should not have been thrown.", e);
        }
    }
    
    static {
        Flushables.class.getName();
        logger = Logger.global;
    }
}
