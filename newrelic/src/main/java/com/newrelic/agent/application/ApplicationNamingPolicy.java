// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.application;

import com.newrelic.api.agent.ApplicationNamePriority;
import com.newrelic.agent.Transaction;

public interface ApplicationNamingPolicy
{
    boolean canSetApplicationName(Transaction p0, ApplicationNamePriority p1);
}
