// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.tracing;

import com.newrelic.agent.util.Strings;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableList;
import com.newrelic.agent.bridge.TransactionNamePriority;
import java.util.Collection;
import java.util.Arrays;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.instrumentation.InstrumentationType;
import java.util.List;

public class TraceDetailsBuilder
{
    private String metricPrefix;
    private String metricName;
    private String tracerFactoryName;
    private boolean dispatcher;
    private boolean excludeFromTransactionTrace;
    private boolean ignoreTransaction;
    private boolean nameTransaction;
    private boolean custom;
    private boolean webTransaction;
    private TransactionName transactionName;
    private List<InstrumentationType> instrumentationTypes;
    private List<String> instrumentationSourceNames;
    private boolean leaf;
    private final List<String> rollupMetricName;
    public List<ParameterAttributeName> parameterAttributeNames;
    
    private TraceDetailsBuilder() {
        this.instrumentationTypes = (List<InstrumentationType>)Lists.newArrayListWithCapacity(3);
        this.instrumentationSourceNames = (List<String>)Lists.newArrayListWithCapacity(3);
        this.rollupMetricName = (List<String>)Lists.newArrayListWithCapacity(5);
    }
    
    public static TraceDetailsBuilder newBuilder() {
        return new TraceDetailsBuilder();
    }
    
    public static TraceDetailsBuilder newBuilder(final TraceDetails traceDetails) {
        final TraceDetailsBuilder builder = new TraceDetailsBuilder();
        builder.custom = traceDetails.isCustom();
        builder.dispatcher = traceDetails.dispatcher();
        builder.excludeFromTransactionTrace = traceDetails.excludeFromTransactionTrace();
        builder.ignoreTransaction = traceDetails.ignoreTransaction();
        builder.instrumentationSourceNames = (List<String>)Lists.newArrayList((Iterable<?>)traceDetails.instrumentationSourceNames());
        builder.instrumentationTypes = (List<InstrumentationType>)Lists.newArrayList((Iterable<?>)traceDetails.instrumentationTypes());
        builder.metricName = traceDetails.metricName();
        builder.metricPrefix = traceDetails.metricPrefix();
        builder.transactionName = traceDetails.transactionName();
        builder.webTransaction = traceDetails.isWebTransaction();
        builder.leaf = traceDetails.isLeaf();
        builder.rollupMetricName.addAll(Arrays.asList(traceDetails.rollupMetricName()));
        builder.parameterAttributeNames = (List<ParameterAttributeName>)Lists.newArrayList((Iterable<?>)traceDetails.getParameterAttributeNames());
        return builder;
    }
    
    public TraceDetails build() {
        return new DefaultTraceDetails(this);
    }
    
    public TraceDetailsBuilder setParameterAttributeNames(final List<ParameterAttributeName> reportedParams) {
        this.parameterAttributeNames = reportedParams;
        return this;
    }
    
    public TraceDetailsBuilder setMetricPrefix(final String metricPrefix) {
        if (metricPrefix == null) {
            this.metricPrefix = null;
        }
        else {
            this.metricPrefix = (metricPrefix.endsWith("/") ? metricPrefix.substring(0, metricPrefix.length() - 1) : metricPrefix);
        }
        return this;
    }
    
    public TraceDetailsBuilder setMetricName(final String metricName) {
        this.metricName = metricName;
        return this;
    }
    
    public TraceDetailsBuilder setTracerFactoryName(final String tracerFactoryName) {
        this.tracerFactoryName = tracerFactoryName;
        return this;
    }
    
    public TraceDetailsBuilder setDispatcher(final boolean dispatcher) {
        this.dispatcher = dispatcher;
        return this;
    }
    
    public TraceDetailsBuilder setCustom(final boolean custom) {
        this.custom = custom;
        return this;
    }
    
    public TraceDetailsBuilder setLeaf(final boolean leaf) {
        this.leaf = leaf;
        return this;
    }
    
    public TraceDetailsBuilder setExcludeFromTransactionTrace(final boolean excludeFromTransactionTrace) {
        this.excludeFromTransactionTrace = excludeFromTransactionTrace;
        return this;
    }
    
    public TraceDetailsBuilder setIgnoreTransaction(final boolean ignoreTransaction) {
        this.ignoreTransaction = ignoreTransaction;
        return this;
    }
    
    public TraceDetailsBuilder setNameTransaction(final boolean nameTransaction) {
        this.nameTransaction = nameTransaction;
        return this;
    }
    
    public TraceDetailsBuilder setTransactionName(final TransactionNamePriority namingPriority, final boolean override, final String category, final String path) {
        this.transactionName = new TransactionName(namingPriority, override, category, path);
        return this;
    }
    
    public TraceDetailsBuilder setInstrumentationType(final InstrumentationType type) {
        if (type == null) {
            this.instrumentationTypes = Lists.newArrayList(InstrumentationType.Unknown);
        }
        else {
            this.instrumentationTypes = Lists.newArrayList(type);
        }
        return this;
    }
    
    public TraceDetailsBuilder setInstrumentationSourceName(final String instrumentationSourceName) {
        if (instrumentationSourceName == null) {
            this.instrumentationSourceNames = Lists.newArrayList("Unknown");
        }
        else {
            this.instrumentationSourceNames = Lists.newArrayList(instrumentationSourceName);
        }
        return this;
    }
    
    public TraceDetailsBuilder setWebTransaction(final boolean webTransaction) {
        this.webTransaction = webTransaction;
        return this;
    }
    
    public TraceDetailsBuilder addRollupMetricName(final String metricName) {
        this.rollupMetricName.add(metricName);
        return this;
    }
    
    public TraceDetailsBuilder merge(final TraceDetails otherDetails) {
        if (this.metricPrefix == null) {
            this.metricPrefix = otherDetails.metricPrefix();
        }
        if (this.metricName == null) {
            this.metricName = otherDetails.metricName();
        }
        if (this.tracerFactoryName == null) {
            this.tracerFactoryName = otherDetails.tracerFactoryName();
        }
        if (!this.dispatcher) {
            this.dispatcher = otherDetails.dispatcher();
        }
        if (!this.excludeFromTransactionTrace) {
            this.excludeFromTransactionTrace = otherDetails.excludeFromTransactionTrace();
        }
        if (!this.ignoreTransaction && !this.custom) {
            this.ignoreTransaction = otherDetails.ignoreTransaction();
        }
        if (this.transactionName == null) {
            this.transactionName = otherDetails.transactionName();
        }
        if (!this.custom) {
            this.custom = otherDetails.isCustom();
            if (!this.leaf) {
                this.leaf = otherDetails.isLeaf();
            }
        }
        if (!this.webTransaction) {
            this.webTransaction = otherDetails.isWebTransaction();
        }
        this.rollupMetricName.addAll(Arrays.asList(otherDetails.rollupMetricName()));
        this.instrumentationTypes.addAll(otherDetails.instrumentationTypes());
        this.instrumentationSourceNames.addAll(otherDetails.instrumentationSourceNames());
        this.parameterAttributeNames.addAll(otherDetails.getParameterAttributeNames());
        return this;
    }
    
    public static TraceDetails merge(final TraceDetails existing, final TraceDetails trace) {
        if (trace.isCustom()) {
            return newBuilder(trace).merge(existing).build();
        }
        return newBuilder(existing).merge(trace).build();
    }
    
    private static final class DefaultTraceDetails implements TraceDetails
    {
        private final String metricPrefix;
        private final String metricName;
        private final String tracerFactoryName;
        private final TransactionName transactionName;
        private final boolean dispatcher;
        private final boolean excludeFromTransactionTrace;
        private final boolean ignoreTransaction;
        private final boolean custom;
        private final boolean webTransaction;
        private final List<InstrumentationType> instrumentationTypes;
        private final List<String> instrumentationSourceNames;
        private final boolean leaf;
        private final String[] rollupMetricNames;
        private final List<ParameterAttributeName> parameterAttributeNames;
        
        protected DefaultTraceDetails(final TraceDetailsBuilder builder) {
            this.metricName = builder.metricName;
            this.metricPrefix = builder.metricPrefix;
            this.tracerFactoryName = builder.tracerFactoryName;
            this.dispatcher = builder.dispatcher;
            this.excludeFromTransactionTrace = builder.excludeFromTransactionTrace;
            this.ignoreTransaction = builder.ignoreTransaction;
            this.custom = builder.custom;
            if (builder.nameTransaction) {
                this.transactionName = (this.custom ? TransactionName.CUSTOM_DEFAULT : TransactionName.BUILT_IN_DEFAULT);
            }
            else {
                this.transactionName = builder.transactionName;
            }
            this.instrumentationSourceNames = (List<String>)Lists.newArrayList((Iterable<?>)builder.instrumentationSourceNames);
            this.instrumentationTypes = (List<InstrumentationType>)Lists.newArrayList((Iterable<?>)builder.instrumentationTypes);
            this.webTransaction = builder.webTransaction;
            this.leaf = builder.leaf;
            this.rollupMetricNames = builder.rollupMetricName.toArray(new String[0]);
            this.parameterAttributeNames = (List<ParameterAttributeName>)((builder.parameterAttributeNames == null) ? ImmutableList.of() : builder.parameterAttributeNames);
        }
        
        public boolean isLeaf() {
            return this.leaf;
        }
        
        public String metricName() {
            return this.metricName;
        }
        
        public boolean dispatcher() {
            return this.dispatcher;
        }
        
        public String tracerFactoryName() {
            return this.tracerFactoryName;
        }
        
        public boolean excludeFromTransactionTrace() {
            return this.excludeFromTransactionTrace;
        }
        
        public String metricPrefix() {
            return this.metricPrefix;
        }
        
        public String getFullMetricName(final String className, final String methodName) {
            if (this.metricName != null) {
                return this.metricName;
            }
            if (this.metricPrefix == null) {
                return null;
            }
            return Strings.join('/', this.metricPrefix, "${className}", methodName);
        }
        
        public boolean ignoreTransaction() {
            return this.ignoreTransaction;
        }
        
        public boolean isCustom() {
            return this.custom;
        }
        
        public TransactionName transactionName() {
            return this.transactionName;
        }
        
        public List<InstrumentationType> instrumentationTypes() {
            return this.instrumentationTypes;
        }
        
        public List<String> instrumentationSourceNames() {
            return this.instrumentationSourceNames;
        }
        
        public boolean isWebTransaction() {
            return this.webTransaction;
        }
        
        public String toString() {
            return "DefaultTraceDetails [transactionName=" + this.transactionName + ", dispatcher=" + this.dispatcher + ", custom=" + this.custom + ", instrumentationType=" + this.instrumentationTypes + ", instrumentationSourceName=" + this.instrumentationSourceNames + "]";
        }
        
        public String[] rollupMetricName() {
            return this.rollupMetricNames;
        }
        
        public List<ParameterAttributeName> getParameterAttributeNames() {
            return this.parameterAttributeNames;
        }
    }
}
