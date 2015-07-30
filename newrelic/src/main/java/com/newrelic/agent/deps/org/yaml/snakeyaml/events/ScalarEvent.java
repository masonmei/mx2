// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.events;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;

public final class ScalarEvent extends NodeEvent
{
    private final String tag;
    private final Character style;
    private final String value;
    private final boolean[] implicit;
    
    public ScalarEvent(final String anchor, final String tag, final boolean[] implicit, final String value, final Mark startMark, final Mark endMark, final Character style) {
        super(anchor, startMark, endMark);
        this.tag = tag;
        this.implicit = implicit;
        this.value = value;
        this.style = style;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public Character getStyle() {
        return this.style;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public boolean[] getImplicit() {
        return this.implicit;
    }
    
    protected String getArguments() {
        return super.getArguments() + ", tag=" + this.tag + ", implicit=[" + this.implicit[0] + ", " + this.implicit[1] + "], value=" + this.value;
    }
}
