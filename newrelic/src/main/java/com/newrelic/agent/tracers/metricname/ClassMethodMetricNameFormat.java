// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.metricname;

import com.newrelic.agent.util.Strings;
import com.newrelic.agent.tracers.ClassMethodSignature;

public class ClassMethodMetricNameFormat extends AbstractMetricNameFormat
{
    private String metricName;
    private final ClassMethodSignature signature;
    private final String className;
    private final String prefix;
    
    public ClassMethodMetricNameFormat(final ClassMethodSignature sig, final Object object) {
        this(sig, object, "Java");
    }
    
    public ClassMethodMetricNameFormat(final ClassMethodSignature sig, final Object object, final String prefix) {
        this.signature = sig;
        this.className = ((object == null) ? sig.getClassName() : object.getClass().getName());
        this.prefix = prefix;
    }
    
    public String getMetricName() {
        if (this.metricName == null) {
            this.metricName = Strings.join('/', this.prefix, this.className, this.signature.getMethodName());
        }
        return this.metricName;
    }
    
    public static String getMetricName(final ClassMethodSignature sig, final Object object) {
        return getMetricName(sig, object, "Java");
    }
    
    public static String getMetricName(final ClassMethodSignature sig, final Object object, final String prefix) {
        if (object == null) {
            return getMetricName(sig, prefix);
        }
        return Strings.join('/', prefix, object.getClass().getName(), sig.getMethodName());
    }
    
    public static String getMetricName(final ClassMethodSignature sig, final String prefix) {
        final String className = sig.getClassName().replaceAll("/", ".");
        return Strings.join('/', prefix, className, sig.getMethodName());
    }
}
