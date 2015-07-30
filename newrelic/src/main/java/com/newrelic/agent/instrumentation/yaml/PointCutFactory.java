// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.yaml;

import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.extension.ConfigurationConstruct;
import com.newrelic.agent.instrumentation.methodmatchers.InvalidMethodDescriptor;
import com.newrelic.agent.instrumentation.methodmatchers.NoMethodsMatcher;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OrClassMatcher;
import java.util.Iterator;
import java.util.ArrayList;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import java.util.Collection;

public class PointCutFactory
{
    private final String defaultMetricPrefix;
    private final ClassLoader classLoader;
    private final String extensionName;
    
    public PointCutFactory(final ClassLoader classLoader, final String metricPrefix, final String name) {
        this.classLoader = classLoader;
        this.defaultMetricPrefix = metricPrefix;
        this.extensionName = name;
    }
    
    public Collection<ExtensionClassAndMethodMatcher> getPointCuts(final Object config) throws ParseException {
        if (config instanceof List) {
            return this.getPointCuts((List)config);
        }
        if (config instanceof Map) {
            return this.getPointCuts((Map)config);
        }
        return (Collection<ExtensionClassAndMethodMatcher>)Collections.EMPTY_LIST;
    }
    
    public ExtensionClassAndMethodMatcher getPointCut(final Object obj) throws ParseException {
        if (obj instanceof String) {
            return this.getPointCut((String)obj);
        }
        if (obj instanceof Map) {
            return this.getPointCut((Map)obj);
        }
        throw new RuntimeException(MessageFormat.format("Unknown pointcut type: {0} ({1}", obj, obj.getClass().getName()));
    }
    
    public ExtensionClassAndMethodMatcher getPointCut(final String string) throws ParseException {
        final ClassMethodSignature sig = parseClassMethodSignature(string);
        if (sig != null) {
            return new ExtensionClassAndMethodMatcher(this.extensionName, null, this.defaultMetricPrefix, new ExactClassMatcher(sig.getClassName()), createExactMethodMatcher(sig.getMethodName(), sig.getMethodDesc()), false, false, false, null);
        }
        throw new RuntimeException("Unable to parse point cut: " + string);
    }
    
    private ExtensionClassAndMethodMatcher getPointCut(final Map attrs) {
        return YmlExtensionPointCutConverter.createExtensionPointCut(attrs, this.defaultMetricPrefix, this.classLoader, this.extensionName);
    }
    
    public List<ExtensionClassAndMethodMatcher> getPointCuts(final List list) throws ParseException {
        final List<ExtensionClassAndMethodMatcher> pcs = new ArrayList<ExtensionClassAndMethodMatcher>();
        for (final Object obj : list) {
            pcs.add(this.getPointCut(obj));
        }
        return pcs;
    }
    
    public List<ExtensionClassAndMethodMatcher> getPointCuts(final Map namesToPointCuts) throws ParseException {
        Collection<Object> values = null;
        if (null != namesToPointCuts) {
            values = namesToPointCuts.values();
        }
        if (null == values) {
            return (List<ExtensionClassAndMethodMatcher>)Collections.EMPTY_LIST;
        }
        final List<ExtensionClassAndMethodMatcher> pcs = new ArrayList<ExtensionClassAndMethodMatcher>();
        for (final Object obj : values) {
            if (obj instanceof String) {
                pcs.add(this.getPointCut((String)obj));
            }
            else {
                if (!(obj instanceof Map)) {
                    continue;
                }
                pcs.add(this.getPointCut((Map)obj));
            }
        }
        return pcs;
    }
    
    static Collection<ClassMatcher> getClassMatchers(final Collection matchers) {
        final Collection<ClassMatcher> list = new ArrayList<ClassMatcher>(matchers.size());
        for (final Object matcher : matchers) {
            list.add(getClassMatcher(matcher));
        }
        return list;
    }
    
    static ClassMatcher getClassMatcher(final Object yaml) {
        if (yaml instanceof ClassMatcher) {
            return (ClassMatcher)yaml;
        }
        if (yaml instanceof String) {
            return new ExactClassMatcher(((String)yaml).trim());
        }
        if (yaml instanceof List) {
            final List list = (List)yaml;
            return OrClassMatcher.getClassMatcher(getClassMatchers(list));
        }
        return null;
    }
    
    static Collection<MethodMatcher> getMethodMatchers(final Collection matchers) {
        final Collection<MethodMatcher> list = new ArrayList<MethodMatcher>(matchers.size());
        for (final Object matcher : matchers) {
            list.add(getMethodMatcher(matcher));
        }
        return list;
    }
    
    static MethodMatcher getMethodMatcher(final Object yaml) {
        MethodMatcher matcher = null;
        if (yaml instanceof MethodMatcher) {
            matcher = (MethodMatcher)yaml;
        }
        else if (yaml instanceof List) {
            final List list = (List)yaml;
            if (!list.isEmpty() && list.get(0) instanceof String && list.get(0).toString().indexOf(40) < 0) {
                return createExactMethodMatcher(list.get(0).toString().trim(), Strings.trim(list.subList(1, list.size())));
            }
            return OrMethodMatcher.getMethodMatcher(getMethodMatchers(list));
        }
        else if (yaml instanceof String) {
            final String text = yaml.toString().trim();
            final int index = text.indexOf(40);
            if (index > 0) {
                final String methodName = text.substring(0, index);
                final String methodDesc = text.substring(index);
                return createExactMethodMatcher(methodName, methodDesc);
            }
            return new ExactMethodMatcher(text, new String[0]);
        }
        return matcher;
    }
    
    public static ClassMethodSignature parseClassMethodSignature(final String signature) {
        final int methodArgIndex = signature.indexOf(40);
        if (methodArgIndex > 0) {
            final String methodDesc = signature.substring(methodArgIndex);
            final String classAndMethod = signature.substring(0, methodArgIndex);
            final int methodStart = classAndMethod.lastIndexOf(46);
            if (methodStart > 0) {
                final String methodName = classAndMethod.substring(methodStart + 1);
                final String className = classAndMethod.substring(0, methodStart).replace('/', '.');
                return new ClassMethodSignature(className, methodName, methodDesc);
            }
        }
        return null;
    }
    
    public static MethodMatcher createExactMethodMatcher(final String methodName, final String methodDesc) {
        final ExactMethodMatcher methodMatcher = new ExactMethodMatcher(methodName, methodDesc);
        return validateMethodMatcher(methodMatcher);
    }
    
    public static MethodMatcher createExactMethodMatcher(final String methodName, final Collection<String> methodDescriptions) {
        final ExactMethodMatcher methodMatcher = new ExactMethodMatcher(methodName, methodDescriptions);
        return validateMethodMatcher(methodMatcher);
    }
    
    private static MethodMatcher validateMethodMatcher(final ExactMethodMatcher methodMatcher) {
        try {
            methodMatcher.validate();
            return methodMatcher;
        }
        catch (InvalidMethodDescriptor e) {
            Agent.LOG.log(Level.SEVERE, MessageFormat.format("The method matcher can not be created, meaning the methods associated with it will not be monitored - {0}", e.toString()));
            Agent.LOG.log(Level.FINER, "Error creating method matcher.", e);
            return new NoMethodsMatcher();
        }
    }
    
    public static Collection<ConfigurationConstruct> getConstructs() {
        return new InstrumentationConstructor().constructs;
    }
    
    public static class ClassMethodNameFormatDescriptor implements MetricNameFormatFactory
    {
        private final String prefix;
        
        public ClassMethodNameFormatDescriptor(final String prefix, final boolean dispatcher) {
            this.prefix = getMetricPrefix(prefix, dispatcher);
        }
        
        public MetricNameFormat getMetricNameFormat(final ClassMethodSignature sig, final Object object, final Object[] args) {
            if (Strings.isEmpty(this.prefix)) {
                return new ClassMethodMetricNameFormat(sig, object);
            }
            return new ClassMethodMetricNameFormat(sig, object, this.prefix);
        }
        
        private static String getMetricPrefix(final String prefix, final boolean dispatcher) {
            if (!dispatcher) {
                return prefix;
            }
            if (prefix.startsWith("OtherTransaction")) {
                return prefix;
            }
            return "OtherTransaction/" + prefix;
        }
    }
}
