// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

class CharSequenceState
{
    final char c;
    int occurrences;
    
    public CharSequenceState(final char c) {
        this.c = c;
        this.occurrences = 1;
    }
    
    void incrementOccurrences() {
        ++this.occurrences;
    }
}
