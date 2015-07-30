// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.events;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public final class MappingEndEvent extends CollectionEndEvent
{
    public MappingEndEvent(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
    }
}
