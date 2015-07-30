// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.config.BaseConfig;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import java.util.Map;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.yaml.MetricNameFormatFactory;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

public class ClassMethodNameFormatPointCut extends TracerFactoryPointCut
{
    private final MetricNameFormatFactory metricNameFormatFactory;
    private final boolean dispatcher;
    private final boolean skipTransactionTrace;
    private final boolean ignoreTransaction;
    
    public ClassMethodNameFormatPointCut(final MetricNameFormatFactory metricNameFormatFactory, final ClassMatcher classMatcher, final MethodMatcher methodMatcher, final boolean dispatcher, final Map configAttributes) {
        super(new PointCutConfiguration((String)null), classMatcher, methodMatcher);
        this.setPriority(19);
        this.metricNameFormatFactory = metricNameFormatFactory;
        this.dispatcher = dispatcher;
        final BaseConfig config = new BaseConfig(configAttributes);
        this.skipTransactionTrace = config.getProperty("skip_transaction_trace", Boolean.FALSE);
        this.ignoreTransaction = config.getProperty("ignore_transaction", Boolean.FALSE);
    }
    
    public ClassMethodNameFormatPointCut(final MetricNameFormatFactory pMetricNameFormatFactory, final ClassMatcher pClassMatcher, final MethodMatcher pMethodMatcher, final boolean pDispatcher, final boolean pSkipTransactionTrace, final boolean pIgnoreTransaction) {
        super(new PointCutConfiguration((String)null), pClassMatcher, pMethodMatcher);
        this.setPriority(19);
        this.metricNameFormatFactory = pMetricNameFormatFactory;
        this.dispatcher = pDispatcher;
        this.skipTransactionTrace = pSkipTransactionTrace;
        this.ignoreTransaction = pIgnoreTransaction;
    }
    
    protected boolean isDispatcher() {
        return this.dispatcher;
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        final MetricNameFormat format = this.metricNameFormatFactory.getMetricNameFormat(sig, object, args);
        if (this.dispatcher) {
            return new OtherRootTracer(transaction, sig, object, format);
        }
        int flags = 2;
        if (!this.skipTransactionTrace) {
            flags |= 0x4;
        }
        return new DefaultTracer(transaction, sig, object, format, flags);
    }
    
    protected boolean isIgnoreTransaction() {
        return this.ignoreTransaction;
    }
}
