// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.struts;

import com.newrelic.agent.instrumentation.pointcuts.InterfaceMixin;

@InterfaceMixin(originalClassName = { "com/opensymphony/xwork2/ActionProxy" })
public interface ActionProxy
{
    String getActionName();
}
