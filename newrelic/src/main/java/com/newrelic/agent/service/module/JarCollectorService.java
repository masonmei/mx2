// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.module;

import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.service.Service;

public interface JarCollectorService extends Service
{
    ClassMatchVisitorFactory getSourceVisitor();
}
