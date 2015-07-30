// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import java.util.logging.Level;
import com.newrelic.api.agent.Logger;

class NoOpLogger implements Logger
{
    static final Logger INSTANCE;
    
    public boolean isLoggable(final Level level) {
        return false;
    }
    
    public void log(final Level level, final String pattern, final Object... parts) {
    }
    
    public void log(final Level level, final Throwable t, final String pattern, final Object... msg) {
    }
    
    public void logToChild(final String childName, final Level level, final String pattern, final Object... parts) {
    }
    
    static {
        INSTANCE = (Logger)new NoOpLogger();
    }
}
