// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.normalization;

import java.util.Set;

public class TransactionSegmentTerms
{
    final String prefix;
    final Set<String> terms;
    
    public TransactionSegmentTerms(final String prefix, final Set<String> terms) {
        this.prefix = (prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix);
        this.terms = terms;
    }
    
    public String toString() {
        return "TransactionSegmentTerms [prefix=" + this.prefix + ", terms=" + this.terms + "]";
    }
}
