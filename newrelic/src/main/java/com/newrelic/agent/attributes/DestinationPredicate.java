// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import com.newrelic.agent.deps.com.google.common.base.Predicate;

public interface DestinationPredicate extends Predicate<String>
{
    boolean isPotentialConfigMatch(String p0);
}
