// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.bridge.ObjectFieldManager;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import java.security.ProtectionDomain;
import com.newrelic.agent.util.asm.BenignClassReadException;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.util.AgentError;
import java.util.jar.JarInputStream;
import java.net.URL;
import com.newrelic.bootstrap.BootstrapAgent;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Closeable;
import java.lang.instrument.ClassDefinition;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import java.io.File;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.concurrent.CountDownLatch;
import java.util.Collection;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.Agent;
import com.newrelic.agent.config.AgentJarHelper;
import java.util.regex.Pattern;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Set;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import java.util.Map;

public class ClassWeaverService
{
    private static final int PARTITONS = 8;
    private final Map<String, InstrumentationPackage> instrumentationPackageNames;
    private final InstrumentationContextManager contextManager;
    private final Set<InstrumentationPackage> instrumentationPackages;
    private final Set<InstrumentationPackage> internalInstrumentationPackages;
    
    public ClassWeaverService(final InstrumentationContextManager contextManager) {
        this.instrumentationPackageNames = (Map<String, InstrumentationPackage>)Maps.newConcurrentMap();
        this.instrumentationPackages = (Set<InstrumentationPackage>)Sets.newCopyOnWriteArraySet();
        this.internalInstrumentationPackages = (Set<InstrumentationPackage>)Sets.newCopyOnWriteArraySet();
        this.contextManager = contextManager;
    }
    
    public InstrumentationContextManager getContextManager() {
        return this.contextManager;
    }
    
    public InstrumentationPackage getInstrumentationPackage(final String implementationTitle) {
        return this.instrumentationPackageNames.get(implementationTitle);
    }
    
    public void removeInstrumentationPackage(final InstrumentationPackage instrumentationPackage) {
        this.instrumentationPackageNames.remove(instrumentationPackage.getImplementationTitle());
        this.contextManager.removeMatchVisitor(instrumentationPackage.getMatcher());
    }
    
    public void addWeavingClassTransformer(final WeavingClassTransformer transformer) {
        this.instrumentationPackageNames.put(transformer.instrumentationPackage.getImplementationTitle(), transformer.instrumentationPackage);
        this.contextManager.addContextClassTransformer(transformer.instrumentationPackage.getMatcher(), transformer);
    }
    
    public Runnable registerInstrumentation() {
        AgentBridge.objectFieldManager = (ObjectFieldManager)new ObjectFieldManagerImpl();
        final Collection<String> jarFileNames = AgentJarHelper.findAgentJarFileNames(Pattern.compile("instrumentation\\/(.*).jar"));
        if (jarFileNames.isEmpty()) {
            Agent.LOG.error("No instrumentation packages were found in the agent.");
        }
        else {
            Agent.LOG.fine("Loading " + jarFileNames.size() + " instrumentation packages.");
        }
        this.addInternalInstrumentationPackagesInParallel(jarFileNames);
        return this.reloadInstrumentationPackages(ServiceFactory.getExtensionService().getWeaveExtensions(), true);
    }
    
    private void addInternalInstrumentationPackagesInParallel(final Collection<String> jarFileNames) {
        final int partitions = (jarFileNames.size() < 8) ? jarFileNames.size() : 8;
        final List<Set<String>> instrumentationPartitions = this.partitionInstrumentationJars(jarFileNames, partitions);
        final CountDownLatch executorCountDown = new CountDownLatch(partitions);
        for (final Set<String> instrumentationJars : instrumentationPartitions) {
            final Runnable instrumentationRunnable = new Runnable() {
                public void run() {
                    ClassWeaverService.this.internalInstrumentationPackages.addAll(ClassWeaverService.this.getInternalInstrumentationPackages(instrumentationJars));
                    executorCountDown.countDown();
                }
            };
            new Thread(instrumentationRunnable).start();
        }
        try {
            executorCountDown.await();
        }
        catch (InterruptedException e) {
            Agent.LOG.log(Level.FINE, "Interrupted while waiting for instrumentation packages.", e);
        }
    }
    
    private List<Set<String>> partitionInstrumentationJars(final Collection<String> jarFileNames, final int partitions) {
        final List<Set<String>> instrumentationPartitions = new ArrayList<Set<String>>(partitions);
        for (int i = 0; i < partitions; ++i) {
            instrumentationPartitions.add(new HashSet<String>());
        }
        int index = 0;
        for (final String jarFileName : jarFileNames) {
            instrumentationPartitions.get(index++ % partitions).add(jarFileName);
        }
        return instrumentationPartitions;
    }
    
    public Runnable reloadInstrumentationPackages(final Collection<File> weaveExtensions) {
        return this.reloadInstrumentationPackages(weaveExtensions, false);
    }
    
    private Runnable reloadInstrumentationPackages(final Collection<File> weaveExtensions, final boolean retransformInternalInstrumentationPackageMatches) {
        final Collection<ClassMatchVisitorFactory> unloadedMatchers = (Collection<ClassMatchVisitorFactory>)Sets.newHashSet();
        final Set<InstrumentationPackage> toClose = (Set<InstrumentationPackage>)Sets.newHashSet((Iterable<?>)this.instrumentationPackages);
        for (final InstrumentationPackage ip : this.instrumentationPackages) {
            unloadedMatchers.add(ip.getMatcher());
            this.removeInstrumentationPackage(ip);
        }
        final Collection<ClassMatchVisitorFactory> loadedMatchers = (Collection<ClassMatchVisitorFactory>)Sets.newHashSet();
        this.instrumentationPackages.clear();
        this.instrumentationPackages.addAll(this.getInstrumentationPackages(weaveExtensions));
        loadedMatchers.addAll(this.buildTransformers(retransformInternalInstrumentationPackageMatches));
        if (!retransformInternalInstrumentationPackageMatches) {
            final Collection<ClassDefinition> existingClasses = (Collection<ClassDefinition>)Lists.newLinkedList();
            for (final InstrumentationPackage ip2 : this.instrumentationPackages) {
                final Iterator<InstrumentationPackage> iterator = toClose.iterator();
                while (iterator.hasNext()) {
                    final InstrumentationPackage oldIp = iterator.next();
                    if (oldIp.getCloseables().isEmpty() || oldIp.getImplementationTitle().equals(ip2.getImplementationTitle())) {
                        iterator.remove();
                    }
                }
                for (final Map.Entry<String, byte[]> className : ip2.newClasses.entrySet()) {
                    try {
                        final Class<?> existingClass = this.getClass().getClassLoader().loadClass(className.getKey().replace("/", "."));
                        existingClasses.add(new ClassDefinition(existingClass, className.getValue()));
                    }
                    catch (ClassNotFoundException ex) {}
                }
            }
            if (!existingClasses.isEmpty() && ServiceFactory.getAgent().getInstrumentation().isRedefineClassesSupported()) {
                try {
                    ServiceFactory.getAgent().getInstrumentation().redefineClasses((ClassDefinition[])existingClasses.toArray(new ClassDefinition[0]));
                }
                catch (Exception e) {
                    if (!Agent.LOG.isFinestEnabled()) {
                        Agent.LOG.fine("Error redefining classes: " + e.getMessage());
                    }
                    else {
                        Agent.LOG.log(Level.FINEST, "Error redefining classes", e);
                    }
                }
            }
        }
        final Collection<ClassMatchVisitorFactory> matchers = (Collection<ClassMatchVisitorFactory>)Sets.newHashSet((Iterable<?>)loadedMatchers);
        matchers.addAll(unloadedMatchers);
        return new Runnable() {
            public void run() {
                ServiceFactory.getClassTransformerService().retransformMatchingClassesImmediately(matchers);
                for (final InstrumentationPackage ip : toClose) {
                    for (final Closeable closeable : ip.getCloseables()) {
                        try {
                            closeable.close();
                        }
                        catch (IOException e) {
                            Agent.LOG.log(Level.FINE, (Throwable)e, "Error closing InstrumentationPackage {0} closeable {1}", new Object[] { ip.implementationTitle, closeable });
                        }
                    }
                }
            }
        };
    }
    
    private Collection<ClassMatchVisitorFactory> buildTransformers(final boolean retransformInternalInstrumentationPackageMatches) {
        final Set<InstrumentationPackage> filteredInstrumentationPackages = this.filter(this.instrumentationPackages, this.internalInstrumentationPackages);
        this.instrumentationPackages.retainAll(filteredInstrumentationPackages);
        if (!retransformInternalInstrumentationPackageMatches) {
            filteredInstrumentationPackages.retainAll(this.instrumentationPackages);
        }
        final List<WeavingClassTransformer> transformers = this.createTransformers(filteredInstrumentationPackages);
        for (final WeavingClassTransformer transformer : transformers) {
            this.addWeavingClassTransformer(transformer);
            transformer.instrumentationPackage.getLogger().debug("Registered " + transformer.instrumentationPackage.getImplementationTitle());
        }
        final Collection<ClassMatchVisitorFactory> matchers = (Collection<ClassMatchVisitorFactory>)Sets.newHashSet();
        if (retransformInternalInstrumentationPackageMatches) {
            for (final WeavingClassTransformer transformer2 : transformers) {
                matchers.add(transformer2.instrumentationPackage.getMatcher());
            }
        }
        else {
            for (final InstrumentationPackage instrumentationPackage : this.instrumentationPackages) {
                matchers.add(instrumentationPackage.getMatcher());
            }
        }
        return matchers;
    }
    
    private List<WeavingClassTransformer> createTransformers(final Set<InstrumentationPackage> instrumentationPackages) {
        final List<WeavingClassTransformer> transformers = (List<WeavingClassTransformer>)Lists.newLinkedList();
        for (final InstrumentationPackage instrumentationPackage : instrumentationPackages) {
            try {
                final WeavingClassTransformer transformer = this.getTransformer(instrumentationPackage, instrumentationPackage.getLocation());
                if (transformer == null) {
                    continue;
                }
                transformers.add(transformer);
            }
            catch (Exception e) {
                instrumentationPackage.getLogger().severe("Unable to load " + instrumentationPackage.getLocation() + " : " + e.getMessage());
                instrumentationPackage.getLogger().log(Level.FINEST, "Unable to load instrumentation jar " + instrumentationPackage.getLocation(), e);
            }
        }
        return transformers;
    }
    
    private Set<InstrumentationPackage> getInstrumentationPackages(final Collection<File> weaveExtensions) {
        final Set<InstrumentationPackage> instrumentationPackages = (Set<InstrumentationPackage>)Sets.newHashSet();
        for (final File file : weaveExtensions) {
            if (!file.exists()) {
                Agent.LOG.error("Unable to find instrumentation jar: " + file.getAbsolutePath());
            }
            else {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(file);
                    this.addInstrumentationPackage(instrumentationPackages, inputStream, file.getAbsolutePath());
                    try {
                        inputStream.close();
                    }
                    catch (IOException ex) {}
                }
                catch (IOException e) {
                    Agent.LOG.severe("Unable to open " + file.getAbsolutePath());
                    try {
                        inputStream.close();
                    }
                    catch (IOException ex2) {}
                }
                finally {
                    try {
                        inputStream.close();
                    }
                    catch (IOException ex3) {}
                }
            }
        }
        return instrumentationPackages;
    }
    
    private Set<InstrumentationPackage> getInternalInstrumentationPackages(final Collection<String> jarFileNames) {
        final Set<InstrumentationPackage> instrumentationPackages = (Set<InstrumentationPackage>)Sets.newHashSet();
        for (final String name : jarFileNames) {
            final URL instrumentationUrl = BootstrapAgent.class.getResource('/' + name);
            if (instrumentationUrl == null) {
                Agent.LOG.error("Unable to find instrumentation jar: " + name);
            }
            else {
                InputStream inputStream = null;
                try {
                    inputStream = instrumentationUrl.openStream();
                    this.addInstrumentationPackage(instrumentationPackages, inputStream, instrumentationUrl.getFile());
                    try {
                        inputStream.close();
                    }
                    catch (IOException ex) {}
                }
                catch (IOException e) {
                    Agent.LOG.severe("Unable to open " + instrumentationUrl.getFile());
                    try {
                        inputStream.close();
                    }
                    catch (IOException ex2) {}
                }
                finally {
                    try {
                        inputStream.close();
                    }
                    catch (IOException ex3) {}
                }
            }
        }
        return instrumentationPackages;
    }
    
    private void addInstrumentationPackage(final Set<InstrumentationPackage> instrumentationPackages, final InputStream inputStream, final String location) {
        IAgentLogger logger = Agent.LOG;
        try {
            try {
                final JarInputStream jarStream = new JarInputStream(inputStream);
                final InstrumentationMetadata metadata = new InstrumentationMetadata(jarStream, location);
                logger = Agent.LOG.getChildLogger(metadata.getImplementationTitle());
                final InstrumentationPackage instrumentationPackage = new InstrumentationPackage(this.getContextManager().getInstrumentation(), logger, metadata, jarStream);
                logger.debug("Loaded " + metadata.getImplementationTitle());
                if (instrumentationPackage.isEnabled()) {
                    instrumentationPackages.add(instrumentationPackage);
                }
                inputStream.close();
            }
            finally {
                inputStream.close();
            }
        }
        catch (Exception e) {
            logger.severe("Unable to load " + location + " : " + e.getMessage());
            logger.log(Level.FINEST, "Unable to load instrumentation " + location, e);
        }
        catch (AgentError e2) {
            logger.severe("Unable to load " + location + " : " + e2.getMessage());
            logger.log(Level.FINEST, "Unable to load instrumentation " + location, e2);
        }
    }
    
    private Set<InstrumentationPackage> filter(final Set<InstrumentationPackage> instrumentationPackages, final Set<InstrumentationPackage> internalInstrumentationPackages) {
        final Set<InstrumentationPackage> filteredPackages = (Set<InstrumentationPackage>)Sets.newHashSet();
        filteredPackages.addAll(instrumentationPackages);
        filteredPackages.addAll(internalInstrumentationPackages);
        final Map<String, InstrumentationPackage> filtered = (Map<String, InstrumentationPackage>)Maps.newHashMap();
        for (final InstrumentationPackage instrumentationPackage : filteredPackages) {
            final InstrumentationPackage existing = filtered.get(instrumentationPackage.getImplementationTitle());
            if (existing != null) {
                if (existing.getImplementationVersion() == instrumentationPackage.getImplementationVersion()) {
                    Agent.LOG.severe(instrumentationPackage.getLocation() + " is named " + instrumentationPackage.getImplementationTitle() + " which conflicts with the title of " + existing.getLocation());
                }
                else if (existing.getImplementationVersion() > instrumentationPackage.getImplementationVersion()) {
                    Agent.LOG.debug(instrumentationPackage.getImplementationTitle() + " v" + instrumentationPackage.getImplementationVersion() + " in " + instrumentationPackage.getLocation() + " is older than version " + existing.getImplementationVersion() + " in " + existing.getLocation());
                }
                else {
                    filtered.put(instrumentationPackage.getImplementationTitle(), instrumentationPackage);
                }
            }
            else {
                filtered.put(instrumentationPackage.getImplementationTitle(), instrumentationPackage);
            }
        }
        filteredPackages.retainAll(filtered.values());
        return filteredPackages;
    }
    
    private WeavingClassTransformer getTransformer(final InstrumentationPackage instrumentationPackage, final String name) {
        final boolean containsJDKClasses = instrumentationPackage.containsJDKClasses();
        return containsJDKClasses ? new BootstrapClassTransformer(instrumentationPackage) : new WeavingClassTransformer(instrumentationPackage);
    }
    
    public void loadClass(final ClassLoader classLoader, final String implementationTitle, final String className) throws IOException {
        final InstrumentationPackage instrumentationPackage = this.getInstrumentationPackage(implementationTitle);
        if (instrumentationPackage != null) {
            final String internalClassName = className.replace('.', '/');
            final byte[] bytes = instrumentationPackage.getClassBytes().get(internalClassName);
            if (bytes != null) {
                ClassAppender.getSystemClassAppender().appendClasses(classLoader, instrumentationPackage.newClasses, instrumentationPackage.newClassLoadOrder);
            }
            else {
                instrumentationPackage.getLogger().fine("Unable to find " + className + " in instrumentation package " + implementationTitle);
            }
        }
        else {
            Agent.LOG.log(Level.FINE, "Unable to find instrumentation package {0} for class {1}.", new Object[] { implementationTitle, className });
        }
    }
    
    public ClassReader getClassReader(final Class<?> theClass) throws BenignClassReadException {
        final WeaveInstrumentation weaveInstrumentation = theClass.getAnnotation(WeaveInstrumentation.class);
        if (weaveInstrumentation == null) {
            return null;
        }
        final InstrumentationPackage instrumentationPackage = this.instrumentationPackageNames.get(weaveInstrumentation.title());
        final byte[] bytes = instrumentationPackage.getClassBytes().get(Type.getInternalName(theClass));
        if (bytes != null) {
            return new ClassReader(bytes);
        }
        throw new BenignClassReadException(theClass.getName() + " is WeaveInstrumentation but could not be found in " + weaveInstrumentation.title());
    }
    
    public void registerInstrumentationCloseable(final String instrumentationName, final Closeable closeable) {
        final InstrumentationPackage instrumentationPackage = this.instrumentationPackageNames.get(instrumentationName);
        if (instrumentationPackage == null) {
            Agent.LOG.log(Level.INFO, "Unable to register closeable {1} for missing instrumentationPackage {0}", new Object[] { instrumentationName, closeable });
            return;
        }
        instrumentationPackage.addCloseable(closeable);
    }
    
    private static class BootstrapClassTransformer extends WeavingClassTransformer
    {
        protected BootstrapClassTransformer(final InstrumentationPackage instrumentationPackage) {
            super(instrumentationPackage);
        }
        
        protected byte[] doTransform(ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer, final InstrumentationContext context, final OptimizedClassMatcher.Match match) throws Exception {
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }
            return super.doTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer, context, match);
        }
    }
}
