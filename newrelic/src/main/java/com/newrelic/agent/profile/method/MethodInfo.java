// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile.method;

import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.instrumentation.InstrumentedMethod;
import java.util.Map;
import java.util.List;

public abstract class MethodInfo
{
    public abstract List<Map<String, Object>> getJsonMethodMaps();
    
    protected static void addOneMethodInstrumentedInfo(final Map<String, Object> toAdd, final InstrumentedMethod instrumentedMethod) {
        if (instrumentedMethod != null) {
            final Map<String, Object> inst = (Map<String, Object>)Maps.newHashMap();
            inst.put("dispatcher", instrumentedMethod.dispatcher());
            addInstrumentationInfo(inst, instrumentedMethod);
            toAdd.put("traced_instrumentation", inst);
        }
    }
    
    private static void addInstrumentationInfo(final Map<String, Object> inst, final InstrumentedMethod instrumentedMethod) {
        final InstrumentationType[] inputTypes = instrumentedMethod.instrumentationTypes();
        final String[] inputNames = instrumentedMethod.instrumentationNames();
        if (inputTypes != null && inputNames != null && inputTypes.length > 0 && inputTypes.length == inputNames.length) {
            final Map<String, List<String>> instrumentedTypes = (Map<String, List<String>>)Maps.newHashMap();
            for (int i = 0; i < inputTypes.length; ++i) {
                if (isTimedInstrumentation(inputTypes[i])) {
                    List<String> names = instrumentedTypes.get(inputTypes[i].toString());
                    if (names == null) {
                        names = (List<String>)Lists.newArrayList();
                        names.add(inputNames[i]);
                        instrumentedTypes.put(inputTypes[i].toString(), names);
                    }
                    else {
                        names.add(inputNames[i]);
                    }
                }
            }
            if (instrumentedTypes.size() > 0) {
                inst.put("types", instrumentedTypes);
            }
        }
    }
    
    private static boolean isTimedInstrumentation(final InstrumentationType type) {
        return type != InstrumentationType.WeaveInstrumentation;
    }
    
    protected static void addOneMethodArgs(final Map<String, Object> toAdd, final List<String> arguments) {
        toAdd.put("args", arguments);
    }
}
