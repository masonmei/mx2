// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.Transaction;

public class ExternalComponentTracer extends AbstractExternalComponentTracer
{
    public ExternalComponentTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final String library, final String uri, final String... operations) {
        this(transaction, sig, object, host, library, false, uri, operations);
    }
    
    public ExternalComponentTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final String library, final boolean includeOperationInMetric, final String uri, final String... operations) {
        super(transaction, sig, object, host, library, includeOperationInMetric, uri, operations);
    }
    
    public ExternalComponentTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final MetricNameFormat metricNameFormat) {
        super(transaction, sig, object, host, metricNameFormat);
    }
}
