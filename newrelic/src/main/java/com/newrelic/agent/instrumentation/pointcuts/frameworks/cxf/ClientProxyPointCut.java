// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.cxf;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.util.Strings;
import java.lang.reflect.Method;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class ClientProxyPointCut extends TracerFactoryPointCut
{
    public ClientProxyPointCut(final ClassTransformer classTransformer) {
        super(ClientProxyPointCut.class, new ExactClassMatcher("org/apache/cxf/frontend/ClientProxy"), PointCut.createExactMethodMatcher("invokeSync", "(Ljava/lang/reflect/Method;Lorg/apache/cxf/service/model/BindingOperationInfo;[Ljava/lang/Object;)Ljava/lang/Object;"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object proxy, final Object[] args) {
        final Method method = (Method)args[0];
        return new DefaultTracer(transaction, sig, proxy, new SimpleMetricNameFormat(Strings.join('/', "Java", method.getDeclaringClass().getName(), method.getName())));
    }
}
