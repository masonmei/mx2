// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import java.util.Iterator;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.List;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;

public class Annotation extends AnnotationVisitor
{
    private Map<String, Object> values;
    private final TraceDetailsBuilder traceDetailsBuilder;
    
    public Annotation(final AnnotationVisitor annotationVisitor, final String desc, final TraceDetailsBuilder traceDetailsBuilder) {
        super(327680, annotationVisitor);
        this.traceDetailsBuilder = traceDetailsBuilder;
    }
    
    public Map<String, Object> getValues() {
        return (this.values == null) ? Collections.<String, Object> emptyMap() : this.values;
    }
    
    public void visit(final String name, final Object value) {
        this.getOrCreateValues().put(name, value);
        super.visit(name, value);
    }
    
    private Map<String, Object> getOrCreateValues() {
        if (this.values == null) {
            this.values = Maps.newHashMap();
        }
        return this.values;
    }
    
    public AnnotationVisitor visitArray(final String name) {
        List<Object> list = (List<Object>) this.getOrCreateValues().get(name);
        if (list == null) {
            list = Lists.newArrayList();
            this.getOrCreateValues().put(name, list);
        }
        final List<Object> theList = list;
        AnnotationVisitor av = super.visitArray(name);
        av = new AnnotationVisitor(327680, av) {
            public void visit(final String name, final Object value) {
                super.visit(name, value);
                theList.add(value);
            }
        };
        return av;
    }
    
    private boolean getBoolean(final String name) {
        final Boolean value = (Boolean) this.getValues().get(name);
        return value != null && value;
    }
    
    public TraceDetails getTraceDetails(final boolean custom) {
        final String metricName = (String) this.getValues().get("metricName");
        final boolean dispatcher = this.getBoolean("dispatcher");
        if (dispatcher && metricName != null) {
            this.traceDetailsBuilder.setTransactionName(TransactionNamePriority.CUSTOM_HIGH, false, "Custom", metricName);
        }
        final List<Object> rollupMetricNames = (List<Object>) this.getValues().get("rollupMetricName");
        if (rollupMetricNames != null) {
            for (final Object v : rollupMetricNames) {
                this.traceDetailsBuilder.addRollupMetricName(v.toString());
            }
        }
        return new DelegatingTraceDetails(this.traceDetailsBuilder.setMetricName(metricName).setDispatcher(dispatcher).setTracerFactoryName((String) this.getValues().get("tracerFactoryName")).setExcludeFromTransactionTrace(this.getBoolean("skipTransactionTrace")).setNameTransaction(this.getBoolean("nameTransaction")).setCustom(custom).setExcludeFromTransactionTrace(this.getBoolean("excludeFromTransactionTrace")).setLeaf(this.getBoolean("leaf")).build()) {
            public String getFullMetricName(final String className, final String methodName) {
                return this.metricName();
            }
        };
    }
}
