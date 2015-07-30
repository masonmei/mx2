// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import com.newrelic.agent.Transaction;

public class DoneState extends AbstractRUMState
{
    public RUMState process(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text) throws Exception {
        this.writeText(tx, generator, node, text);
        return this;
    }
}
