// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.conditional;

import java.lang.reflect.InvocationTargetException;
import org.codehaus.commons.compiler.CompileException;
import java.lang.reflect.Method;
import org.codehaus.janino.ClassBodyEvaluator;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PropertyContainer;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class PropertyEvalScriptBuilder extends ContextAwareBase
{
    private static String SCRIPT_PREFIX;
    private static String SCRIPT_SUFFIX;
    final PropertyContainer localPropContainer;
    Map<String, String> map;
    
    PropertyEvalScriptBuilder(final PropertyContainer localPropContainer) {
        this.map = new HashMap<String, String>();
        this.localPropContainer = localPropContainer;
    }
    
    public Condition build(final String script) throws IllegalAccessException, CompileException, InstantiationException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        final ClassBodyEvaluator cbe = new ClassBodyEvaluator();
        cbe.setImplementedInterfaces(new Class[] { Condition.class });
        cbe.setExtendedClass((Class)PropertyWrapperForScripts.class);
        cbe.cook(PropertyEvalScriptBuilder.SCRIPT_PREFIX + script + PropertyEvalScriptBuilder.SCRIPT_SUFFIX);
        final Class<?> clazz = (Class<?>)cbe.getClazz();
        final Condition instance = (Condition)clazz.newInstance();
        final Method setMapMethod = clazz.getMethod("setPropertyContainers", PropertyContainer.class, PropertyContainer.class);
        setMapMethod.invoke(instance, this.localPropContainer, this.context);
        return instance;
    }
    
    static {
        PropertyEvalScriptBuilder.SCRIPT_PREFIX = "public boolean evaluate() { return ";
        PropertyEvalScriptBuilder.SCRIPT_SUFFIX = "; }";
    }
}
