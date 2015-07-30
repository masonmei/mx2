// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import org.xml.sax.Locator;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Interpreter;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public abstract class Action extends ContextAwareBase
{
    public static final String NAME_ATTRIBUTE = "name";
    public static final String KEY_ATTRIBUTE = "key";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String FILE_ATTRIBUTE = "file";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String PATTERN_ATTRIBUTE = "pattern";
    public static final String SCOPE_ATTRIBUTE = "scope";
    public static final String ACTION_CLASS_ATTRIBUTE = "actionClass";
    
    public abstract void begin(final InterpretationContext p0, final String p1, final Attributes p2) throws ActionException;
    
    public void body(final InterpretationContext ic, final String body) throws ActionException {
    }
    
    public abstract void end(final InterpretationContext p0, final String p1) throws ActionException;
    
    public String toString() {
        return this.getClass().getName();
    }
    
    protected int getColumnNumber(final InterpretationContext ic) {
        final Interpreter ji = ic.getJoranInterpreter();
        final Locator locator = ji.getLocator();
        if (locator != null) {
            return locator.getColumnNumber();
        }
        return -1;
    }
    
    protected int getLineNumber(final InterpretationContext ic) {
        final Interpreter ji = ic.getJoranInterpreter();
        final Locator locator = ji.getLocator();
        if (locator != null) {
            return locator.getLineNumber();
        }
        return -1;
    }
    
    protected String getLineColStr(final InterpretationContext ic) {
        return "line: " + this.getLineNumber(ic) + ", column: " + this.getColumnNumber(ic);
    }
}
