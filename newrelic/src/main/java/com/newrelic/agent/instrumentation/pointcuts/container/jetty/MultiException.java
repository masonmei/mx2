// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.jetty;

import java.util.List;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "org/eclipse/jetty/util/MultiException" })
public interface MultiException
{
    List<Throwable> getThrowables();
}
