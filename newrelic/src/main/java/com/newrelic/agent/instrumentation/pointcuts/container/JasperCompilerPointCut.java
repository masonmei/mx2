// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container;

import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.jasper.GeneratorVisitTracerFactory;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class JasperCompilerPointCut extends TracerFactoryPointCut
{
    public static String CURRENT_JSP_FILE_KEY;
    private static final ClassMatcher CLASS_MATCHER;
    private static final MethodMatcher COMPILE_METHOD_1_MATCHER;
    private static final MethodMatcher COMPILE_METHOD_2_MATCHER;
    private static final MethodMatcher METHOD_MATCHER;
    
    public JasperCompilerPointCut(final ClassTransformer classTransformer) {
        super(JasperCompilerPointCut.class, JasperCompilerPointCut.CLASS_MATCHER, JasperCompilerPointCut.METHOD_MATCHER);
    }
    
    public Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, final Object compiler, final Object[] args) {
        final Tracer parent = tx.getTransactionActivity().getLastTracer();
        if (parent != null && parent instanceof JasperCompilerTracer) {
            return null;
        }
        try {
            final Object context = compiler.getClass().getMethod("getCompilationContext", (Class<?>[])new Class[0]).invoke(compiler, new Object[0]);
            if (context != null) {
                final String page = (String)context.getClass().getMethod("getJspFile", (Class<?>[])new Class[0]).invoke(context, new Object[0]);
                if (page != null) {
                    final String msg = MessageFormat.format("Compiling JSP: {0}", page);
                    Agent.LOG.fine(msg);
                    GeneratorVisitTracerFactory.noticeJspCompile(tx, page);
                    final JasperCompilerTracer tracer = new JasperCompilerTracer(tx, sig, compiler, new SimpleMetricNameFormat("View" + page.replace('.', '_') + "/Compile"));
                    return tracer;
                }
            }
        }
        catch (Throwable t) {
            Agent.LOG.severe("Unable to generate a Jasper compilation metric: " + t.getMessage());
        }
        return null;
    }
    
    static {
        JasperCompilerPointCut.CURRENT_JSP_FILE_KEY = "CurrentJspFileKey";
        CLASS_MATCHER = new ExactClassMatcher("org/apache/jasper/compiler/Compiler");
        COMPILE_METHOD_1_MATCHER = new ExactMethodMatcher("compile", "(ZZ)V");
        COMPILE_METHOD_2_MATCHER = new ExactMethodMatcher("compile", "(Z)V");
        METHOD_MATCHER = OrMethodMatcher.getMethodMatcher(JasperCompilerPointCut.COMPILE_METHOD_1_MATCHER, JasperCompilerPointCut.COMPILE_METHOD_2_MATCHER);
    }
    
    private final class JasperCompilerTracer extends DefaultTracer
    {
        public JasperCompilerTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final MetricNameFormat metricNameFormatter) {
            super(tx, sig, object, metricNameFormatter, 0);
        }
    }
}
