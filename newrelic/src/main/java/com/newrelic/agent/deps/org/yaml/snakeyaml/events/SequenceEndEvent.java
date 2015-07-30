// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.events;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public final class SequenceEndEvent extends CollectionEndEvent
{
    public SequenceEndEvent(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
    }
}
