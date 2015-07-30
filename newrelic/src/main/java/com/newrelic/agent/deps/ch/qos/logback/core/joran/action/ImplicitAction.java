// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Pattern;

public abstract class ImplicitAction extends Action
{
    public abstract boolean isApplicable(final Pattern p0, final Attributes p1, final InterpretationContext p2);
}
