// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.List;
import com.newrelic.agent.instrumentation.annotationmatchers.OrAnnotationMatcher;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.util.ArrayList;
import com.newrelic.agent.instrumentation.annotationmatchers.ClassNameAnnotationMatcher;
import com.newrelic.agent.instrumentation.annotationmatchers.NoMatchAnnotationMatcher;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import com.newrelic.agent.instrumentation.annotationmatchers.AnnotationMatcher;
import java.util.Set;

final class ClassTransformerConfigImpl extends BaseConfig implements ClassTransformerConfig
{
    public static final String ENABLED = "enabled";
    public static final String EXCLUDES = "excludes";
    public static final String INCLUDES = "includes";
    public static final String COMPUTE_FRAMES = "compute_frames";
    public static final String SHUTDOWN_DELAY = "shutdown_delay";
    public static final String GRANT_PACKAGE_ACCESS = "grant_package_access";
    public static final boolean DEFAULT_COMPUTE_FRAMES = true;
    public static final boolean DEFAULT_ENABLED = true;
    public static final int DEFAULT_SHUTDOWN_DELAY = -1;
    public static final boolean DEFAULT_GRANT_PACKAGE_ACCESS = false;
    private static final String SYSTEM_PROPERTY_ROOT = "newrelic.config.class_transformer.";
    static final String NEW_RELIC_TRACE_TYPE_DESC = "Lcom/newrelic/api/agent/Trace;";
    static final String DEPRECATED_NEW_RELIC_TRACE_TYPE_DESC = "Lcom/newrelic/agent/Trace;";
    private final boolean isEnabled;
    private final boolean custom_tracing;
    private final Set<String> excludes;
    private final Set<String> includes;
    private final boolean computeFrames;
    private final long shutdownDelayInNanos;
    private final boolean grantPackageAccess;
    private final AnnotationMatcher ignoreTransactionAnnotationMatcher;
    private final AnnotationMatcher ignoreApdexAnnotationMatcher;
    private final AnnotationMatcher traceAnnotationMatcher;
    public static final String JDBC_STATEMENTS_PROPERTY = "jdbc_statements";
    
    ClassTransformerConfigImpl(final Map<String, Object> props, final boolean customTracingEnabled) {
        super(props, "newrelic.config.class_transformer.");
        this.custom_tracing = customTracingEnabled;
        this.isEnabled = this.getProperty("enabled", true);
        this.excludes = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(this.getUniqueStrings("excludes")));
        this.includes = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(this.getUniqueStrings("includes")));
        this.computeFrames = this.getProperty("compute_frames", true);
        this.shutdownDelayInNanos = this.initShutdownDelay();
        this.grantPackageAccess = this.getProperty("grant_package_access", false);
        this.traceAnnotationMatcher = (customTracingEnabled ? this.initializeTraceAnnotationMatcher(props) : new NoMatchAnnotationMatcher());
        this.ignoreTransactionAnnotationMatcher = new ClassNameAnnotationMatcher("NewRelicIgnoreTransaction", false);
        this.ignoreApdexAnnotationMatcher = new ClassNameAnnotationMatcher("NewRelicIgnoreApdex", false);
    }
    
    private AnnotationMatcher initializeTraceAnnotationMatcher(final Map<?, ?> props) {
        final List<AnnotationMatcher> matchers = new ArrayList<AnnotationMatcher>();
        matchers.add(new ClassNameAnnotationMatcher(Type.getType("Lcom/newrelic/agent/Trace;").getDescriptor()));
        matchers.add(new ClassNameAnnotationMatcher(Type.getType("Lcom/newrelic/api/agent/Trace;").getDescriptor()));
        final String traceAnnotationClassName = this.getProperty("trace_annotation_class_name");
        if (traceAnnotationClassName == null) {
            matchers.add(new ClassNameAnnotationMatcher("NewRelicTrace", false));
        }
        else {
            final HashSet<String> names = new HashSet<String>();
            for (final String name : traceAnnotationClassName.split(",")) {
                Agent.LOG.fine("Adding " + name + " as a Trace annotation");
                names.add(internalizeName(name));
            }
            matchers.add(new AnnotationMatcher() {
                public boolean matches(final String annotationDesc) {
                    return names.contains(annotationDesc);
                }
            });
        }
        return OrAnnotationMatcher.getOrMatcher((AnnotationMatcher[])matchers.toArray(new AnnotationMatcher[0]));
    }
    
    static String internalizeName(final String name) {
        return 'L' + name.trim().replace('.', '/') + ';';
    }
    
    private long initShutdownDelay() {
        final int shutdownDelayInSeconds = this.getIntProperty("shutdown_delay", -1);
        if (shutdownDelayInSeconds > 0) {
            return TimeUnit.NANOSECONDS.convert(shutdownDelayInSeconds, TimeUnit.SECONDS);
        }
        return -1L;
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public boolean isCustomTracingEnabled() {
        return this.custom_tracing;
    }
    
    public Set<String> getIncludes() {
        return this.includes;
    }
    
    public Set<String> getExcludes() {
        return this.excludes;
    }
    
    public boolean computeFrames() {
        return this.computeFrames;
    }
    
    public boolean isGrantPackageAccess() {
        return this.grantPackageAccess;
    }
    
    public long getShutdownDelayInNanos() {
        return this.shutdownDelayInNanos;
    }
    
    public final AnnotationMatcher getIgnoreTransactionAnnotationMatcher() {
        return this.ignoreTransactionAnnotationMatcher;
    }
    
    public final AnnotationMatcher getIgnoreApdexAnnotationMatcher() {
        return this.ignoreApdexAnnotationMatcher;
    }
    
    public AnnotationMatcher getTraceAnnotationMatcher() {
        return this.traceAnnotationMatcher;
    }
    
    public Collection<String> getJdbcStatements() {
        final String jdbcStatementsProp = this.getProperty("jdbc_statements");
        if (jdbcStatementsProp == null) {
            return (Collection<String>)Collections.emptyList();
        }
        return Arrays.asList(jdbcStatementsProp.split(",[\\s]*"));
    }
    
    static ClassTransformerConfig createClassTransformerConfig(Map<String, Object> settings, final boolean custom_tracing) {
        if (settings == null) {
            settings = Collections.emptyMap();
        }
        return new ClassTransformerConfigImpl(settings, custom_tracing);
    }
    
    public Config getInstrumentationConfig(final String implementationTitle) {
        Map<String, Object> config = Collections.emptyMap();
        if (implementationTitle != null) {
            final Object pointCutConfig = this.getProperty(implementationTitle);
            if (pointCutConfig instanceof Map) {
                config = (Map<String, Object>)pointCutConfig;
            }
        }
        return new BaseConfig(config);
    }
}
