// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.spring;

import com.newrelic.agent.instrumentation.pointcuts.MethodMapper;
import java.lang.reflect.Method;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMapper;

@InterfaceMapper(className = { "org/springframework/web/method/HandlerMethod" }, originalInterfaceName = "org/springframework/web/method/HandlerMethod")
public interface HandlerMethod
{
    @MethodMapper(originalMethodName = "getBridgedMethod", originalDescriptor = "()Ljava/lang/reflect/Method;", invokeInterface = false)
    Method _nr_getBridgedMethod();
    
    @MethodMapper(originalMethodName = "getBean", originalDescriptor = "()Ljava/lang/Object;", invokeInterface = false)
    Object _nr_getBean();
}
