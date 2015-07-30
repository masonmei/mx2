// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import java.io.IOException;
import com.newrelic.agent.util.asm.Utils;
import java.util.Collection;
import java.util.Arrays;
import com.newrelic.agent.stats.StatsService;
import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.stats.StatsWorks;
import java.text.MessageFormat;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.tracing.BridgeUtils;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.util.asm.ClassStructure;
import com.newrelic.agent.util.asm.ClassResolver;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.lang.instrument.IllegalClassFormatException;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import java.security.ProtectionDomain;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;

public class WeavingClassTransformer implements ContextClassTransformer
{
    protected final InstrumentationPackage instrumentationPackage;
    
    protected WeavingClassTransformer(final InstrumentationPackage instrumentationPackage) {
        this.instrumentationPackage = instrumentationPackage;
    }
    
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer, final InstrumentationContext context, final OptimizedClassMatcher.Match match) throws IllegalClassFormatException {
        try {
            return this.doTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer, context, match);
        }
        catch (Throwable t) {
            Agent.LOG.log(Level.SEVERE, "Unable to transform class " + className + ".  Error: " + t.toString());
            Agent.LOG.log(Level.FINE, t.toString(), t);
            return null;
        }
    }
    
    protected byte[] doTransform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer, final InstrumentationContext context, final OptimizedClassMatcher.Match match) throws Exception {
        final String classMatch = this.instrumentationPackage.getClassMatch(match);
        if (classMatch == null) {
            return null;
        }
        final Verifier verifier = this.instrumentationPackage.getVerifier();
        if (!verifier.isEnabled(loader)) {
            return null;
        }
        final MixinClassVisitor mixinClassVisitor = this.instrumentationPackage.getMixinClassVisitor(className, classMatch);
        if (null != mixinClassVisitor) {
            if (!verifier.isVerified(loader) && !verifier.verify(this.instrumentationPackage.getClassAppender(), loader, this.instrumentationPackage.getClassBytes(), this.instrumentationPackage.newClassLoadOrder)) {
                return null;
            }
            try {
                final ClassReader reader = new ClassReader(classfileBuffer);
                if ((reader.getAccess() & 0x200) != 0x0) {
                    if (!MatchType.Interface.equals((Object)mixinClassVisitor.getMatchType())) {
                        this.instrumentationPackage.getLogger().severe(className + " is an interface, but it is not marked with the " + Weave.class.getSimpleName() + " annotation of type Interface");
                    }
                    return null;
                }
                if (this.instrumentationPackage.getLogger().isFinerEnabled()) {
                    this.instrumentationPackage.getLogger().finer("Modifying " + className + " methods " + mixinClassVisitor.getMethods().keySet());
                }
                context.addClassResolver(this.instrumentationPackage);
                ClassVisitor cv;
                final ClassWriter writer = (ClassWriter)(cv = this.instrumentationPackage.getClassWriter(2, loader));
                final ClassStructure classStructure = ClassStructure.getClassStructure(new ClassReader(context.getOriginalClassBytes()), 15);
                final ClassWeaver classWeaver = (ClassWeaver)(cv = new ClassWeaver(cv, mixinClassVisitor, className, verifier, classStructure, context, this.instrumentationPackage, match));
                if (mixinClassVisitor.interfaces.length > 0) {
                    final List<String> interfaces = Lists.newArrayList(mixinClassVisitor.interfaces);
                    interfaces.remove(BridgeUtils.WEAVER_TYPE.getInternalName());
                    this.removeExistingInterfaces(this.instrumentationPackage, loader, reader, interfaces);
                    if (!interfaces.isEmpty()) {
                        this.instrumentationPackage.getLogger().severe(this.instrumentationPackage.getImplementationTitle() + " error.  " + className + " cannot add interfaces " + interfaces);
                        return null;
                    }
                }
                reader.accept(cv, 4);
                final StatsService statsService = ServiceFactory.getStatsService();
                statsService.doStatsWork(StatsWorks.getRecordMetricWork(MessageFormat.format("Supportability/WeaveInstrumentation/WeaveClass/{0}/{1}", this.instrumentationPackage.getImplementationTitle(), className), 1.0f));
                return writer.toByteArray();
            }
            catch (SkipTransformException e) {
                this.instrumentationPackage.getLogger().severe(e.getMessage());
                this.instrumentationPackage.getLogger().log(Level.FINE, "Skip transform", e);
            }
            catch (Throwable t) {
                this.instrumentationPackage.getLogger().severe("Unable to transform " + className + ".  " + t.getMessage());
                this.instrumentationPackage.getLogger().log(Level.FINE, t.getMessage(), t);
            }
        }
        return null;
    }
    
    private void removeExistingInterfaces(final InstrumentationPackage instrumentationPackage, final ClassLoader loader, final ClassReader reader, final List<String> interfaces) {
        if (reader == null) {
            return;
        }
        interfaces.removeAll(Arrays.asList(reader.getInterfaces()));
        for (final String interfaceName : reader.getInterfaces()) {
            try {
                this.removeExistingInterfaces(instrumentationPackage, loader, Utils.readClass(loader, interfaceName), interfaces);
            }
            catch (IOException e) {
                instrumentationPackage.getLogger().log(Level.FINER, "Unable to remove interface " + interfaceName + " from " + reader.getClassName(), e);
            }
        }
        if (!"java/lang/Object".equals(reader.getSuperName())) {
            try {
                this.removeExistingInterfaces(instrumentationPackage, loader, Utils.readClass(loader, reader.getSuperName()), interfaces);
            }
            catch (IOException e2) {
                instrumentationPackage.getLogger().log(Level.FINER, "Unable to remove super class " + reader.getSuperName() + " from " + reader.getClassName(), e2);
            }
        }
    }
    
    public String toString() {
        return this.instrumentationPackage.getImplementationTitle();
    }
}
