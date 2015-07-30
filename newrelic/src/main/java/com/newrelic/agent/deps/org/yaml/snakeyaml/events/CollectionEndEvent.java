// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.events;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public abstract class CollectionEndEvent extends Event
{
    public CollectionEndEvent(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
    }
}
