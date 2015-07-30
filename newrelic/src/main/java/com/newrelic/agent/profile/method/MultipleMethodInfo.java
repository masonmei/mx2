// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile.method;

import java.util.Iterator;
import java.lang.reflect.AnnotatedElement;
import com.newrelic.agent.instrumentation.InstrumentedMethod;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Member;
import java.util.Set;

public class MultipleMethodInfo extends MethodInfo
{
    private final Set<Member> possibleMethods;
    
    public MultipleMethodInfo(final Set<Member> methods) {
        this.possibleMethods = methods;
    }
    
    public List<Map<String, Object>> getJsonMethodMaps() {
        final List<Map<String, Object>> methodList = (List<Map<String, Object>>)Lists.newArrayList();
        for (final Member current : this.possibleMethods) {
            final Map<String, Object> oneMethod = (Map<String, Object>)Maps.newHashMap();
            MethodInfo.addOneMethodArgs(oneMethod, MethodInfoUtil.getArguments(current));
            MethodInfo.addOneMethodInstrumentedInfo(oneMethod, ((AnnotatedElement)current).getAnnotation(InstrumentedMethod.class));
            methodList.add(oneMethod);
        }
        return methodList;
    }
}
