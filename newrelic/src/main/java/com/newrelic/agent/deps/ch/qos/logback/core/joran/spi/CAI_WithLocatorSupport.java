// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import org.xml.sax.Locator;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareImpl;

class CAI_WithLocatorSupport extends ContextAwareImpl
{
    CAI_WithLocatorSupport(final Interpreter interpreter) {
        super(interpreter);
    }
    
    protected Object getOrigin() {
        final Interpreter i = (Interpreter)super.getOrigin();
        final Locator locator = i.locator;
        if (locator != null) {
            return Interpreter.class.getName() + "@" + locator.getLineNumber() + ":" + locator.getColumnNumber();
        }
        return Interpreter.class.getName() + "@NA:NA";
    }
}
