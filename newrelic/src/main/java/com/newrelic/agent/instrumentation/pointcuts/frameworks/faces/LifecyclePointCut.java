// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.faces;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ChildClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class LifecyclePointCut extends TracerFactoryPointCut
{
    private static final String METHOD_DESCRIPTION = "(Ljavax/faces/context/FacesContext;)V";
    
    public LifecyclePointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration(LifecyclePointCut.class), OrClassMatcher.getClassMatcher(new ExactClassMatcher("com/sun/faces/lifecycle/LifecycleImpl"), new ExactClassMatcher("com/sun/faces/mock/MockLifecycle"), new ChildClassMatcher("javax/faces/lifecycle/Lifecycle")), OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher("execute", "(Ljavax/faces/context/FacesContext;)V"), new ExactMethodMatcher("render", "(Ljavax/faces/context/FacesContext;)V")));
        classTransformer.getClassNameFilter().addIncludeRegex("^(com/sun/faces/|javax/faces/)(.*)");
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object lifecycle, final Object[] args) {
        return new DefaultTracer(transaction, sig, lifecycle, new ClassMethodMetricNameFormat(sig, lifecycle));
    }
}
