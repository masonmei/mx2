// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.events;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public final class MappingStartEvent extends CollectionStartEvent
{
    public MappingStartEvent(final String anchor, final String tag, final boolean implicit, final Mark startMark, final Mark endMark, final Boolean flowStyle) {
        super(anchor, tag, implicit, startMark, endMark, flowStyle);
    }
}
