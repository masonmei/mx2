// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.spring;

import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "org/springframework/web/portlet/ModelAndView", "org/springframework/web/servlet/ModelAndView" })
public interface ModelAndView
{
    String getViewName();
}
