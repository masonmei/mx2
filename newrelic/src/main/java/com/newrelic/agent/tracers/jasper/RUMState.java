// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import com.newrelic.agent.Transaction;

public interface RUMState
{
    RUMState process(Transaction p0, GenerateVisitor p1, TemplateText p2, String p3) throws Exception;
}
