// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.io.PrintStream;

public class OnConsoleStatusListener extends OnPrintStreamStatusListenerBase
{
    protected PrintStream getPrintStream() {
        return System.out;
    }
    
    public static void addNewInstanceToContext(final Context context) {
        final OnConsoleStatusListener onConsoleStatusListener = new OnConsoleStatusListener();
        onConsoleStatusListener.setContext(context);
        onConsoleStatusListener.start();
        context.getStatusManager().add(onConsoleStatusListener);
    }
}
