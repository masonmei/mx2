// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.bridge.AgentBridge;

public class ClassLoaderCheck
{
    private static final String CLASSNAME;
    
    public static void loadAgentClass(final ClassLoader loader) throws Throwable {
        if (loader != null) {
            loader.loadClass(ClassLoaderCheck.CLASSNAME);
        }
    }
    
    static {
        CLASSNAME = AgentBridge.class.getName();
    }
}
