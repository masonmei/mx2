// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.lang.instrument.Instrumentation;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import java.util.Collection;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import com.newrelic.agent.instrumentation.custom.ClassRetransformer;
import com.newrelic.agent.service.Service;

public interface ClassTransformerService extends Service
{
    ClassTransformer getClassTransformer();
    
    ClassRetransformer getLocalRetransformer();
    
    ClassRetransformer getRemoteRetransformer();
    
    void checkShutdown();
    
    InstrumentationContextManager getContextManager();
    
    boolean addTraceMatcher(ClassAndMethodMatcher p0, String p1);
    
    void retransformMatchingClasses(Collection<ClassMatchVisitorFactory> p0);
    
    void retransformMatchingClassesImmediately(Collection<ClassMatchVisitorFactory> p0);
    
    Instrumentation getExtensionInstrumentation();
}
