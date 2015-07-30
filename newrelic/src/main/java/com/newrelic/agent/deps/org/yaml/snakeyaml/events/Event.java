// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.events;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public abstract class Event
{
    private final Mark startMark;
    private final Mark endMark;
    
    public Event(final Mark startMark, final Mark endMark) {
        this.startMark = startMark;
        this.endMark = endMark;
    }
    
    public String toString() {
        return "<" + this.getClass().getName() + "(" + this.getArguments() + ")>";
    }
    
    public Mark getStartMark() {
        return this.startMark;
    }
    
    public Mark getEndMark() {
        return this.endMark;
    }
    
    protected String getArguments() {
        return "";
    }
    
    public boolean equals(final Object obj) {
        return obj instanceof Event && this.toString().equals(obj.toString());
    }
}
