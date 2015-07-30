// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.custom;

import com.newrelic.agent.util.Strings;
import com.newrelic.agent.instrumentation.tracing.DelegatingTraceDetails;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.instrumentation.tracing.ParameterAttributeName;
import java.util.List;
import com.newrelic.agent.extension.beans.Extension;
import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.TraceClassAndMethodMatcher;

public class ExtensionClassAndMethodMatcher implements TraceClassAndMethodMatcher
{
    private final ClassMatcher classMatcher;
    private final MethodMatcher methodMatcher;
    private final TraceDetails traceDetails;
    
    public ExtensionClassAndMethodMatcher(final Extension extension, final Extension.Instrumentation.Pointcut pointcut, final String metricPrefix, final ClassMatcher classMatcher, final MethodMatcher methodMatcher, final boolean custom, final List<ParameterAttributeName> reportedParams, final InstrumentationType instType) {
        final String metricName = pointcut.getMetricNameFormat();
        this.classMatcher = classMatcher;
        this.methodMatcher = methodMatcher;
        boolean webTransaction = false;
        if ("web".equals(pointcut.getTransactionType())) {
            webTransaction = true;
        }
        this.traceDetails = TraceDetailsBuilder.newBuilder().setMetricName(metricName).setMetricPrefix(this.getMetricPrefix(metricName, metricPrefix)).setNameTransaction(pointcut.getNameTransaction() != null).setIgnoreTransaction(pointcut.isIgnoreTransaction()).setExcludeFromTransactionTrace(pointcut.isExcludeFromTransactionTrace()).setDispatcher(pointcut.isTransactionStartPoint()).setCustom(custom).setWebTransaction(webTransaction).setInstrumentationSourceName(pointcut.getClass().getName()).setInstrumentationType(instType).setInstrumentationSourceName(extension.getName()).setParameterAttributeNames(reportedParams).build();
    }
    
    private String getMetricPrefix(final String metricName, final String metricPrefix) {
        if (metricName != null) {
            return null;
        }
        return metricPrefix;
    }
    
    public ExtensionClassAndMethodMatcher(final String extensioName, final String metricName, final String metricPrefix, final ClassMatcher classMatcher, final MethodMatcher methodMatcher, final boolean dispatcher, final boolean skipTransTrace, final boolean ignoreTrans, final String tracerFactoryName) {
        this.classMatcher = classMatcher;
        this.methodMatcher = methodMatcher;
        this.traceDetails = TraceDetailsBuilder.newBuilder().setMetricName(metricName).setMetricPrefix(this.getMetricPrefix(metricName, metricPrefix)).setDispatcher(dispatcher).setExcludeFromTransactionTrace(skipTransTrace).setIgnoreTransaction(ignoreTrans).setInstrumentationSourceName(extensioName).setInstrumentationType(InstrumentationType.CustomYaml).setTracerFactoryName(tracerFactoryName).setCustom(true).build();
    }
    
    public ClassMatcher getClassMatcher() {
        return this.classMatcher;
    }
    
    public MethodMatcher getMethodMatcher() {
        return this.methodMatcher;
    }
    
    public TraceDetails getTraceDetails() {
        final String metricName = this.traceDetails.metricName();
        final String metricPrefix = this.traceDetails.metricPrefix();
        return new DelegatingTraceDetails(this.traceDetails) {
            public String getFullMetricName(final String pClassName, final String pMethodName) {
                if (metricPrefix == null && metricName == null) {
                    return null;
                }
                if (metricPrefix == null) {
                    return this.getStringWhenMetricPrefixNull();
                }
                if (metricName == null) {
                    return Strings.join('/', metricPrefix, "${className}", pMethodName);
                }
                return Strings.join('/', metricPrefix, metricName);
            }
            
            private String getStringWhenMetricPrefixNull() {
                if (this.dispatcher()) {
                    return metricName;
                }
                if (metricName.startsWith("OtherTransaction")) {
                    return metricName;
                }
                if (metricName.startsWith("/")) {
                    return "OtherTransaction" + metricName;
                }
                return Strings.join('/', "OtherTransaction", metricName);
            }
        };
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.classMatcher == null) ? 0 : this.classMatcher.hashCode());
        result = 31 * result + ((this.methodMatcher == null) ? 0 : this.methodMatcher.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ExtensionClassAndMethodMatcher other = (ExtensionClassAndMethodMatcher)obj;
        if (this.classMatcher == null) {
            if (other.classMatcher != null) {
                return false;
            }
        }
        else if (!this.classMatcher.equals(other.classMatcher)) {
            return false;
        }
        if (this.methodMatcher == null) {
            if (other.methodMatcher != null) {
                return false;
            }
        }
        else if (!this.methodMatcher.equals(other.methodMatcher)) {
            return false;
        }
        return true;
    }
}
