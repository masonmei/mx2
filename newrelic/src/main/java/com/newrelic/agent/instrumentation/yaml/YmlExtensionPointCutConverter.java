// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.yaml;

import com.newrelic.api.agent.MethodTracerFactory;
import com.newrelic.agent.tracers.TracerFactory;
import com.newrelic.agent.TracerFactoryException;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.config.Config;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.config.BaseConfig;
import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import java.util.Map;

public class YmlExtensionPointCutConverter
{
    public static final String CLASS_MATCHER_KEY = "class_matcher";
    public static final String METHOD_MATCHER_KEY = "method_matcher";
    public static final String DISPATCHER_KEY = "dispatcher";
    public static final String METRIC_NAME_FORMAT_KEY = "metric_name_format";
    public static final String SKIP_TRANS_KEY = "skip_transaction_trace";
    public static final String IGNORE_TRANS_KEY = "ignore_transaction";
    public static final String TRACER_FACTOR_KEY = "tracer_factory";
    
    public static ExtensionClassAndMethodMatcher createExtensionPointCut(final Map attrs, final String defaultMetricPrefix, final ClassLoader classLoader, final String extName) {
        final ClassMatcher classMatcher = getClassMatcher(attrs);
        final MethodMatcher methodMatcher = getMethodMatcher(attrs);
        final boolean dispatcher = getDispatcher(attrs);
        final Config newConfig = new BaseConfig(attrs);
        final boolean skipTransTrace = newConfig.getProperty("skip_transaction_trace", Boolean.FALSE);
        final boolean ignoreTrans = newConfig.getProperty("ignore_transaction", Boolean.FALSE);
        final Object format = attrs.get("metric_name_format");
        String metricName;
        if (format instanceof String) {
            metricName = format.toString();
        }
        else if (null == format) {
            metricName = null;
        }
        else {
            if (!(format instanceof MetricNameFormatFactory)) {
                throw new RuntimeException(MessageFormat.format("Unsupported {0} value", "metric_name_format"));
            }
            Agent.LOG.log(Level.WARNING, MessageFormat.format("The object property {0} is no longer supported in the agent. The default naming mechanism will be used.", "metric_name_format"));
            metricName = null;
        }
        final String tracerFactoryNameString = getTracerFactoryName(attrs, defaultMetricPrefix, dispatcher, format, classLoader);
        final String nameOfExtension = (extName == null) ? "Unknown" : extName;
        return new ExtensionClassAndMethodMatcher(nameOfExtension, metricName, defaultMetricPrefix, classMatcher, methodMatcher, dispatcher, skipTransTrace, ignoreTrans, tracerFactoryNameString);
    }
    
    private static ClassMatcher getClassMatcher(final Map attrs) {
        final ClassMatcher classMatcher = PointCutFactory.getClassMatcher(attrs.get("class_matcher"));
        if (classMatcher == null) {
            throw new RuntimeException("No class matcher for " + attrs.toString());
        }
        return classMatcher;
    }
    
    private static MethodMatcher getMethodMatcher(final Map attrs) {
        final MethodMatcher methodMatcher = PointCutFactory.getMethodMatcher(attrs.get("method_matcher"));
        if (methodMatcher == null) {
            throw new RuntimeException("No method matcher for " + attrs.toString());
        }
        return methodMatcher;
    }
    
    private static boolean getDispatcher(final Map attrs) {
        final Object dispatcherProp = attrs.get("dispatcher");
        return dispatcherProp != null && Boolean.parseBoolean(dispatcherProp.toString());
    }
    
    private static String getTracerFactoryName(final Map attrs, final String prefix, final boolean dispatcher, final Object metricNameFormat, final ClassLoader loader) {
        String tracerFactoryNameString = null;
        final Object tracerFactoryName = attrs.get("tracer_factory");
        if (tracerFactoryName != null) {
            try {
                final TracerFactory factory = getTracerFactory(tracerFactoryName.toString(), loader, new TracerFactoryConfiguration(prefix, dispatcher, metricNameFormat, attrs));
                tracerFactoryNameString = tracerFactoryName.toString();
                ServiceFactory.getTracerService().registerTracerFactory(tracerFactoryNameString, factory);
            }
            catch (TracerFactoryException ex) {
                throw new RuntimeException("Unable to create tracer factory " + tracerFactoryName, ex);
            }
        }
        return tracerFactoryNameString;
    }
    
    public static TracerFactory getTracerFactory(final String tracerFactoryName, final ClassLoader classLoader, final TracerFactoryConfiguration config) throws TracerFactoryException {
        try {
            final Class clazz = classLoader.loadClass(tracerFactoryName);
            final String msg = MessageFormat.format("Instantiating custom tracer factory {0}", tracerFactoryName);
            Agent.LOG.finest(msg);
            if (TracerFactory.class.isAssignableFrom(clazz)) {
                return instantiateTracerFactory(clazz, config);
            }
            if (MethodTracerFactory.class.isAssignableFrom(clazz)) {
                return instantiateMethodTracerFactory(clazz);
            }
            throw new TracerFactoryException("Unknown tracer factory type:" + tracerFactoryName);
        }
        catch (Exception ex) {
            throw new TracerFactoryException("Unable to load tracer factory " + tracerFactoryName, ex);
        }
    }
    
    private static TracerFactory instantiateMethodTracerFactory(final Class clazz) throws Exception {
        final MethodTracerFactory factory = clazz.newInstance();
        return new CustomTracerFactory(factory);
    }
    
    private static TracerFactory instantiateTracerFactory(final Class<? extends TracerFactory> clazz, final TracerFactoryConfiguration config) throws TracerFactoryException {
        try {
            return (TracerFactory)clazz.getConstructor(TracerFactoryConfiguration.class).newInstance(config);
        }
        catch (Exception e) {
            try {
                return (TracerFactory)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new TracerFactoryException("Unable to instantiate tracer factory " + clazz.getName(), e);
            }
        }
    }
}
