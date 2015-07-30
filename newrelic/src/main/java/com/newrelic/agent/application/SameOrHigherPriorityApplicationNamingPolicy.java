// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.application;

import com.newrelic.api.agent.ApplicationNamePriority;
import com.newrelic.agent.Transaction;

public class SameOrHigherPriorityApplicationNamingPolicy extends AbstractApplicationNamingPolicy
{
    private static final SameOrHigherPriorityApplicationNamingPolicy INSTANCE;
    
    public boolean canSetApplicationName(final Transaction transaction, final ApplicationNamePriority priority) {
        final PriorityApplicationName pan = transaction.getPriorityApplicationName();
        return priority.compareTo((Enum)pan.getPriority()) >= 0;
    }
    
    public static SameOrHigherPriorityApplicationNamingPolicy getInstance() {
        return SameOrHigherPriorityApplicationNamingPolicy.INSTANCE;
    }
    
    static {
        INSTANCE = new SameOrHigherPriorityApplicationNamingPolicy();
    }
}
