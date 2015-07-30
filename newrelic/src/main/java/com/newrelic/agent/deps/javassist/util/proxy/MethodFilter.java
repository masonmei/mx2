// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.util.proxy;

import java.lang.reflect.Method;

public interface MethodFilter
{
    boolean isHandled(Method p0);
}
