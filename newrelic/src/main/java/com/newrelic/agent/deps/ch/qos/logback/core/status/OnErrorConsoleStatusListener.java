// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

import java.io.PrintStream;

public class OnErrorConsoleStatusListener extends OnPrintStreamStatusListenerBase
{
    protected PrintStream getPrintStream() {
        return System.err;
    }
}
