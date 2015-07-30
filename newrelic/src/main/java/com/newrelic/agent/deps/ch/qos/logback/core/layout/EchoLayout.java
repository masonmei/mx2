// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.layout;

import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import com.newrelic.agent.deps.ch.qos.logback.core.LayoutBase;

public class EchoLayout<E> extends LayoutBase<E>
{
    public String doLayout(final E event) {
        return event + CoreConstants.LINE_SEPARATOR;
    }
}
