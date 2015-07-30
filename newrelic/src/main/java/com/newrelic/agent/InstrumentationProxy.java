// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.net.URL;
import com.newrelic.agent.util.Streams;
import com.newrelic.agent.util.BootstrapLoader;
import com.newrelic.agent.util.asm.Utils;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.List;
import com.newrelic.agent.instrumentation.ClassTransformer;
import java.util.ArrayList;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.instrument.ClassDefinition;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import java.lang.instrument.Instrumentation;
import com.newrelic.agent.util.InstrumentationWrapper;

public class InstrumentationProxy extends InstrumentationWrapper
{
    private final boolean bootstrapClassIntrumentationEnabled;
    
    protected InstrumentationProxy(final Instrumentation instrumentation, final boolean enableBootstrapClassInstrumentationDefault) {
        super(instrumentation);
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.bootstrapClassIntrumentationEnabled = config.getProperty("enable_bootstrap_class_instrumentation", enableBootstrapClassInstrumentationDefault);
    }
    
    public static InstrumentationProxy getInstrumentationProxy(final Instrumentation inst) {
        if (inst == null) {
            return null;
        }
        return new InstrumentationProxy(inst, true);
    }
    
    protected Instrumentation getInstrumentation() {
        return this.delegate;
    }
    
    public void redefineClasses(final ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {
        if (this.isRedefineClassesSupported()) {
            super.redefineClasses(definitions);
        }
    }
    
    public Class<?>[] retransformUninstrumentedClasses(final String... classNames) throws UnmodifiableClassException, ClassNotFoundException {
        if (!this.isRetransformClassesSupported()) {
            return (Class<?>[])new Class[0];
        }
        final List<Class<?>> classList = new ArrayList<Class<?>>(classNames.length);
        for (final String className : classNames) {
            final Class<?> clazz = Class.forName(className);
            if (!ClassTransformer.isInstrumented(clazz)) {
                classList.add(clazz);
            }
        }
        final Class<?>[] classArray = classList.toArray(new Class[0]);
        if (!classList.isEmpty()) {
            this.retransformClasses(classArray);
        }
        return classArray;
    }
    
    public int getClassReaderFlags() {
        return 8;
    }
    
    public final boolean isBootstrapClassInstrumentationEnabled() {
        return this.bootstrapClassIntrumentationEnabled;
    }
    
    public boolean isAppendToClassLoaderSearchSupported() {
        return true;
    }
    
    public static void forceRedefinition(final Instrumentation instrumentation, final Class<?>... classes) throws ClassNotFoundException, UnmodifiableClassException {
        final List<ClassDefinition> toRedefine = Lists.newArrayList();
        for (final Class<?> clazz : classes) {
            final String classResourceName = Utils.getClassResourceName(clazz);
            URL resource = clazz.getResource(classResourceName);
            if (resource == null) {
                resource = BootstrapLoader.get().getBootstrapResource(classResourceName);
            }
            if (resource != null) {
                try {
                    final byte[] classfileBuffer = Streams.read(resource.openStream(), true);
                    toRedefine.add(new ClassDefinition(clazz, classfileBuffer));
                }
                catch (Exception e) {
                    Agent.LOG.finer("Unable to redefine " + clazz.getName() + " - " + e.toString());
                }
            }
            else {
                Agent.LOG.finer("Unable to find resource to redefine " + clazz.getName());
            }
        }
        if (!toRedefine.isEmpty()) {
            instrumentation.redefineClasses((ClassDefinition[])toRedefine.toArray(new ClassDefinition[0]));
        }
    }
}
