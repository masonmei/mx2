// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.classic.Logger;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public class RootLoggerAction extends Action
{
    Logger root;
    boolean inError;
    
    public RootLoggerAction() {
        this.inError = false;
    }
    
    public void begin(final InterpretationContext ec, final String name, final Attributes attributes) {
        this.inError = false;
        final LoggerContext loggerContext = (LoggerContext)this.context;
        this.root = loggerContext.getLogger("ROOT");
        final String levelStr = ec.subst(attributes.getValue("level"));
        if (!OptionHelper.isEmpty(levelStr)) {
            final Level level = Level.toLevel(levelStr);
            this.addInfo("Setting level of ROOT logger to " + level);
            this.root.setLevel(level);
        }
        ec.pushObject(this.root);
    }
    
    public void end(final InterpretationContext ec, final String name) {
        if (this.inError) {
            return;
        }
        final Object o = ec.peekObject();
        if (o != this.root) {
            this.addWarn("The object on the top the of the stack is not the root logger");
            this.addWarn("It is: " + o);
        }
        else {
            ec.popObject();
        }
    }
    
    public void finish(final InterpretationContext ec) {
    }
}
