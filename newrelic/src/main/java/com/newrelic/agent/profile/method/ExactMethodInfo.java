// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile.method;

import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.Map;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import com.newrelic.agent.instrumentation.InstrumentedMethod;
import java.util.List;

public class ExactMethodInfo extends MethodInfo
{
    private final List<String> arguments;
    private final InstrumentedMethod annotation;
    
    public ExactMethodInfo(final List<String> pArguments, final Member method) {
        this.arguments = pArguments;
        this.annotation = ((AnnotatedElement)method).getAnnotation(InstrumentedMethod.class);
    }
    
    public List<Map<String, Object>> getJsonMethodMaps() {
        final List<Map<String, Object>> methodList = (List<Map<String, Object>>)Lists.newArrayList();
        final Map<String, Object> oneMethod = (Map<String, Object>)Maps.newHashMap();
        MethodInfo.addOneMethodArgs(oneMethod, this.arguments);
        MethodInfo.addOneMethodInstrumentedInfo(oneMethod, this.annotation);
        methodList.add(oneMethod);
        return methodList;
    }
}
