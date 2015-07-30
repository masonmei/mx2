// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.concurrent.Executor;

public class SynchronousExecutor implements Executor
{
    public void execute(final Runnable command) {
        command.run();
    }
}
