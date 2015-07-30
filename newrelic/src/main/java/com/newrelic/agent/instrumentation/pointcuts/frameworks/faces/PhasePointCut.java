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
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCut;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

public class PhasePointCut extends TracerFactoryPointCut
{
    public PhasePointCut() {
        super(new PointCutConfiguration(PhasePointCut.class), new ExactClassMatcher("com/sun/faces/lifecycle/Phase"), PointCut.createExactMethodMatcher("doPhase", "(Ljavax/faces/context/FacesContext;Ljavax/faces/lifecycle/Lifecycle;Ljava/util/ListIterator;)V"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object phase, final Object[] args) {
        return new DefaultTracer(transaction, sig, phase, new ClassMethodMetricNameFormat(sig, phase));
    }
}
