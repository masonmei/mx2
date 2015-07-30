// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.application;

import com.newrelic.api.agent.ApplicationNamePriority;
import com.newrelic.agent.Transaction;

public abstract class AbstractApplicationNamingPolicy implements ApplicationNamingPolicy
{
    public abstract boolean canSetApplicationName(final Transaction p0, final ApplicationNamePriority p1);
}
