// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.dispatchers.Dispatcher;

public interface TransactionActivityInitiator
{
    Dispatcher createDispatcher();
}
