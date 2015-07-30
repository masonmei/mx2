// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import java.lang.instrument.IllegalClassFormatException;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.security.ProtectionDomain;
import java.lang.instrument.ClassFileTransformer;
import com.newrelic.agent.InstrumentationProxy;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.pointcuts.InterfaceMapper;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.logging.IAgentLogger;

public abstract class AbstractImplementationClassTransformer implements StartableClassFileTransformer
{
    protected final IAgentLogger logger;
    protected final int classreaderFlags;
    protected final ClassTransformer classTransformer;
    private final ClassMatcher classMatcher;
    protected final Class interfaceToImplement;
    protected final String originalInterface;
    protected final Type originalInterfaceType;
    private final boolean enabled;
    private final ClassMatcher skipClassMatcher;
    
    public AbstractImplementationClassTransformer(final ClassTransformer classTransformer, final boolean enabled, final Class interfaceToImplement, final ClassMatcher classMatcher, final ClassMatcher skipMatcher, final String originalInterfaceName) {
        this.logger = Agent.LOG.getChildLogger(ClassTransformer.class);
        this.classMatcher = classMatcher;
        this.skipClassMatcher = skipMatcher;
        this.interfaceToImplement = interfaceToImplement;
        this.originalInterface = originalInterfaceName;
        this.originalInterfaceType = Type.getObjectType(this.originalInterface);
        this.enabled = enabled;
        this.classTransformer = classTransformer;
        this.classreaderFlags = classTransformer.getClassReaderFlags();
    }
    
    public AbstractImplementationClassTransformer(final ClassTransformer classTransformer, final boolean enabled, final Class interfaceToImplement) {
        this(classTransformer, enabled, interfaceToImplement, getClassMatcher(interfaceToImplement), getSkipClassMatcher(interfaceToImplement), getOriginalInterface(interfaceToImplement));
    }
    
    private static String getOriginalInterface(final Class interfaceToImplement) {
        final InterfaceMapper interfaceMapper = interfaceToImplement.getAnnotation(InterfaceMapper.class);
        return interfaceMapper.originalInterfaceName();
    }
    
    private static ClassMatcher getClassMatcher(final Class interfaceToImplement) {
        final InterfaceMapper interfaceMapper = interfaceToImplement.getAnnotation(InterfaceMapper.class);
        if (interfaceMapper.className().length == 0) {
            return new ExactClassMatcher(interfaceMapper.originalInterfaceName());
        }
        return ExactClassMatcher.or(interfaceMapper.className());
    }
    
    private static ClassMatcher getSkipClassMatcher(final Class interfaceToImplement) {
        final InterfaceMapper interfaceMapper = interfaceToImplement.getAnnotation(InterfaceMapper.class);
        return ExactClassMatcher.or(interfaceMapper.skip());
    }
    
    public void start(final InstrumentationProxy instrumentation, final boolean isRetransformSupported) {
        if (this.enabled) {
            instrumentation.addTransformer(this, false);
        }
    }
    
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
        final boolean isLoggable = false;
        if (classBeingRedefined != null) {
            return null;
        }
        if (loader == null && Agent.class.getClassLoader() != null) {
            return null;
        }
        final ClassReader cr = new ClassReader(classfileBuffer);
        if (InstrumentationUtils.isInterface(cr)) {
            return null;
        }
        if (this.skipClassMatcher.isMatch(loader, cr)) {
            return null;
        }
        final boolean matches = this.classMatcher.isMatch(loader, cr);
        try {
            if (!matches) {
                if (!this.isGenericInterfaceSupportEnabled()) {
                    return null;
                }
                if (this.excludeClass(className)) {
                    return null;
                }
                if (!this.matches(cr, this.originalInterface)) {
                    return null;
                }
            }
            if (!InstrumentationUtils.isAbleToResolveAgent(loader, className)) {
                if (Agent.LOG.isLoggable(Level.FINER)) {
                    final String msg = MessageFormat.format("Not instrumenting {0}: class loader unable to load agent classes", className);
                    Agent.LOG.log(Level.FINER, msg);
                }
                return null;
            }
            if ("org/eclipse/jetty/server/Request".equals(className)) {
                className.hashCode();
            }
            final byte[] classBytesWithUID = InstrumentationUtils.generateClassBytesWithSerialVersionUID(cr, this.classreaderFlags, loader);
            final ClassReader crWithUID = new ClassReader(classBytesWithUID);
            final ClassWriter cwWithUID = InstrumentationUtils.getClassWriter(crWithUID, loader);
            cr.accept(this.createClassVisitor(crWithUID, cwWithUID, className, loader), this.classreaderFlags);
            return cwWithUID.toByteArray();
        }
        catch (StopProcessingException e) {
            final String msg2 = MessageFormat.format("Instrumentation aborted for {0} - {1} ", className, e);
            Agent.LOG.finer(msg2);
            return null;
        }
        catch (Throwable t) {
            final String msg2 = MessageFormat.format("Instrumentation error for {0} - {1} ", className, t);
            Agent.LOG.finer(msg2);
            return null;
        }
    }
    
    protected boolean excludeClass(final String className) {
        return this.classTransformer.isExcluded(className);
    }
    
    protected boolean isGenericInterfaceSupportEnabled() {
        return true;
    }
    
    protected int getClassReaderFlags() {
        return this.classreaderFlags;
    }
    
    protected abstract ClassVisitor createClassVisitor(final ClassReader p0, final ClassWriter p1, final String p2, final ClassLoader p3);
    
    private boolean matches(final ClassReader cr, final String interfaceNameToMatch) {
        final String[] interfaces = cr.getInterfaces();
        if (interfaces != null) {
            for (final String interfaceName : interfaces) {
                if (interfaceNameToMatch.equals(interfaceName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
