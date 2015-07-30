// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;

public abstract class NamedConverter extends ClassicConverter
{
    Abbreviator abbreviator;
    
    public NamedConverter() {
        this.abbreviator = null;
    }
    
    protected abstract String getFullyQualifiedName(final ILoggingEvent p0);
    
    public void start() {
        final String optStr = this.getFirstOption();
        if (optStr != null) {
            try {
                final int targetLen = Integer.parseInt(optStr);
                if (targetLen == 0) {
                    this.abbreviator = new ClassNameOnlyAbbreviator();
                }
                else if (targetLen > 0) {
                    this.abbreviator = new TargetLengthBasedClassNameAbbreviator(targetLen);
                }
            }
            catch (NumberFormatException ex) {}
        }
    }
    
    public String convert(final ILoggingEvent event) {
        final String fqn = this.getFullyQualifiedName(event);
        if (this.abbreviator == null) {
            return fqn;
        }
        return this.abbreviator.abbreviate(fqn);
    }
}
