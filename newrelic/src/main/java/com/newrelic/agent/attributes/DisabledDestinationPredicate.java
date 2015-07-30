// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

public class DisabledDestinationPredicate implements DestinationPredicate
{
    public boolean apply(final String input) {
        return false;
    }
    
    public boolean isPotentialConfigMatch(final String key) {
        return false;
    }
}
