// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.struts;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class XWork2ResultPC extends TracerFactoryPointCut
{
    public XWork2ResultPC(final ClassTransformer classTransformer) {
        super(XWork2ResultPC.class, new InterfaceMatcher("com/opensymphony/xwork2/Result"), PointCut.createExactMethodMatcher("execute", "(Lcom/opensymphony/xwork2/ActionInvocation;)V"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object result, final Object[] args) {
        String name;
        try {
            Object action;
            if (args[0] instanceof ActionInvocation) {
                action = ((ActionInvocation)args[0]).getAction();
            }
            else {
                action = args[0].getClass().getMethod("getAction", (Class<?>[])new Class[0]).invoke(args[0], new Object[0]);
            }
            name = action.getClass().getName();
        }
        catch (Throwable t) {
            name = "Unknown";
        }
        return new DefaultTracer(transaction, sig, result, new SimpleMetricNameFormat("StrutsResult/" + name));
    }
}
