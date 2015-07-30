// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks;

import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

public abstract class AbstractPortletPointCut extends TracerFactoryPointCut
{
    public AbstractPortletPointCut(final Class<? extends TracerFactoryPointCut> tracerFactory, final MethodMatcher methodMatcher) {
        super(tracerFactory, new InterfaceMatcher("javax/portlet/Portlet"), methodMatcher);
    }
}
