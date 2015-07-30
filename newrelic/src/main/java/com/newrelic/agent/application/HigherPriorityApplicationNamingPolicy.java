// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.application;

import com.newrelic.api.agent.ApplicationNamePriority;
import com.newrelic.agent.Transaction;

public class HigherPriorityApplicationNamingPolicy extends AbstractApplicationNamingPolicy
{
    private static final HigherPriorityApplicationNamingPolicy INSTANCE;
    
    public boolean canSetApplicationName(final Transaction transaction, final ApplicationNamePriority priority) {
        final PriorityApplicationName pan = transaction.getPriorityApplicationName();
        return priority.compareTo((Enum)pan.getPriority()) > 0;
    }
    
    public static HigherPriorityApplicationNamingPolicy getInstance() {
        return HigherPriorityApplicationNamingPolicy.INSTANCE;
    }
    
    static {
        INSTANCE = new HigherPriorityApplicationNamingPolicy();
    }
}
