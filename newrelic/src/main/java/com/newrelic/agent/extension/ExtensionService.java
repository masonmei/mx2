// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import java.util.ArrayList;
import java.io.InputStream;
import java.io.FileInputStream;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.ConfigFileHelper;
import java.io.FileFilter;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import com.newrelic.agent.deps.com.google.common.collect.Collections2;
import com.newrelic.agent.deps.com.google.common.base.Predicate;
import java.util.Arrays;
import com.newrelic.agent.instrumentation.custom.ClassRetransformer;
import com.newrelic.agent.jmx.JmxService;
import java.util.HashMap;
import com.newrelic.agent.reinstrument.ReinstrumentUtils;
import com.newrelic.agent.reinstrument.ReinstrumentResult;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.io.IOException;
import com.newrelic.agent.config.AgentJarHelper;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.stats.StatsEngine;
import java.util.Iterator;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.Collections;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.service.Service;
import java.io.File;
import java.util.Collection;
import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import java.util.List;
import java.util.Set;
import java.util.Map;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.AbstractService;

public class ExtensionService extends AbstractService implements HarvestListener
{
    private ExtensionParsers extensionParsers;
    private final Map<String, Extension> internalExtensions;
    private volatile Set<Extension> extensions;
    private final List<ExtensionClassAndMethodMatcher> pointCuts;
    private final Collection<File> weaveExtensions;
    private final List<Service> services;
    private final List<ConfigurationConstruct> constructs;
    private long lastReloaded;
    private long lastReloadedWeaveInstrumentation;
    private int elementCount;
    private int weaveElementCount;
    
    public ExtensionService() {
        super(ExtensionService.class.getSimpleName());
        this.internalExtensions = (Map<String, Extension>)Maps.newHashMap();
        this.extensions = Collections.emptySet();
        this.pointCuts = (List<ExtensionClassAndMethodMatcher>)Lists.newArrayList();
        this.weaveExtensions = (Collection<File>)Lists.newArrayList();
        this.services = (List<Service>)Lists.newArrayList();
        this.constructs = (List<ConfigurationConstruct>)Lists.newArrayList();
        this.lastReloaded = 0L;
        this.lastReloadedWeaveInstrumentation = 0L;
        this.elementCount = -1;
        this.weaveElementCount = 0;
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    protected void doStart() {
        if (this.isEnabled()) {
            this.extensionParsers = new ExtensionParsers(this.constructs);
            try {
                this.initializeBuiltInExtensions();
                this.loadExtensionJars();
                this.reloadCustomExtensionsIfModified();
                this.reloadWeaveInstrumentationIfModified();
            }
            catch (NoSuchMethodError e) {
                Agent.LOG.error("Unable to initialize agent extensions.  The likely cause is duplicate copies of javax.xml libraries.");
                Agent.LOG.log(Level.FINE, e.toString(), e);
            }
        }
    }
    
    protected void doStop() {
        this.internalExtensions.clear();
        this.pointCuts.clear();
        this.weaveExtensions.clear();
        for (final Service service : this.services) {
            try {
                service.stop();
            }
            catch (Exception e) {
                final String msg = MessageFormat.format("Unable to stop extension service \"{0}\" - {1}", service.getName(), e.toString());
                Agent.LOG.severe(msg);
                this.getLogger().log(Level.FINE, msg, e);
            }
        }
        this.services.clear();
    }
    
    public void beforeHarvest(final String pAppName, final StatsEngine pStatsEngine) {
    }
    
    public void afterHarvest(final String pAppName) {
        if (!ServiceFactory.getConfigService().getDefaultAgentConfig().getApplicationName().equals(pAppName)) {
            return;
        }
        if (ServiceFactory.getAgent().getInstrumentation().isRetransformClassesSupported()) {
            this.reloadCustomExtensionsIfModified();
            this.reloadWeaveInstrumentationIfModified();
        }
        else {
            Agent.LOG.log(Level.FINEST, "Retransformation is not supported - not reloading extensions.");
        }
    }
    
    protected void addInternalExtensionForTesting(final Extension ext) {
        this.internalExtensions.put(ext.getName(), ext);
    }
    
    private void initializeBuiltInExtensions() {
        final String jarFileName = AgentJarHelper.getAgentJarFileName();
        if (jarFileName == null) {
            this.getLogger().log(Level.SEVERE, "Unable to find the agent jar file");
            return;
        }
        try {
            final JarExtension jarExtension = JarExtension.create(this.getLogger(), this.extensionParsers, jarFileName);
            this.addJarExtensions(jarExtension);
        }
        catch (IOException e) {
            this.getLogger().severe(MessageFormat.format("Unable to read extensions from the agent jar : {0}", e.toString()));
            this.getLogger().log(Level.FINER, "Extensions error", e);
        }
    }
    
    private void loadExtensionJars() {
        final Collection<JarExtension> jarExtensions = this.loadJarExtensions(this.getExtensionDirectory());
        for (final JarExtension extension : jarExtensions) {
            if (extension.isWeaveInstrumentation()) {
                continue;
            }
            try {
                for (final Class<?> clazz : extension.getClasses()) {
                    this.noticeExtensionClass(clazz);
                }
                this.addJarExtensions(extension);
            }
            catch (Throwable t) {
                Agent.LOG.log(Level.INFO, "An error occurred adding extension {0} : {1}", new Object[] { extension.getFile(), t.getMessage() });
                Agent.LOG.log(Level.FINEST, t, t.getMessage(), new Object[0]);
            }
        }
    }
    
    private void addJarExtensions(final JarExtension jarExtension) {
        for (final Extension extension : jarExtension.getExtensions().values()) {
            final Extension validateExtension = this.validateExtension(extension, this.internalExtensions);
            if (validateExtension != null) {
                this.internalExtensions.put(extension.getName(), extension);
            }
        }
    }
    
    private void reloadCustomExtensionsIfModified() {
        final File[] xmlFiles = this.getExtensionFiles(ExtensionFileTypes.XML.getFilter());
        final File[] ymlFiles = this.getExtensionFiles(ExtensionFileTypes.YML.getFilter());
        boolean fileModified = xmlFiles.length + ymlFiles.length != this.elementCount;
        if (!fileModified) {
            for (final File file : xmlFiles) {
                fileModified |= (this.lastReloaded < file.lastModified());
            }
            for (final File file : ymlFiles) {
                fileModified |= (this.lastReloaded < file.lastModified());
            }
        }
        if (fileModified) {
            this.lastReloaded = System.currentTimeMillis();
            this.elementCount = xmlFiles.length + ymlFiles.length;
            this.pointCuts.clear();
            final HashMap<String, Extension> allExtensions = Maps.newHashMap((Map<? extends String, ? extends Extension>)this.internalExtensions);
            this.loadValidExtensions(xmlFiles, this.extensionParsers.getXmlParser(), allExtensions);
            this.loadValidExtensions(ymlFiles, this.extensionParsers.getYamlParser(), allExtensions);
            final Set<Extension> externalExtensions = (Set<Extension>)Sets.newHashSet((Iterable<?>)allExtensions.values());
            externalExtensions.removeAll(this.internalExtensions.values());
            final Set<Extension> oldExtensions = this.extensions;
            this.extensions = Collections.unmodifiableSet((Set<? extends Extension>)externalExtensions);
            final JmxService jmxService = ServiceFactory.getJmxService();
            if (jmxService != null) {
                jmxService.reloadExtensions(oldExtensions, this.extensions);
            }
            for (final Extension extension : allExtensions.values()) {
                this.pointCuts.addAll(extension.getInstrumentationMatchers());
            }
            final ClassRetransformer retransformer = ServiceFactory.getClassTransformerService().getLocalRetransformer();
            if (retransformer != null) {
                final Class<?>[] allLoadedClasses = (Class<?>[])ServiceFactory.getAgent().getInstrumentation().getAllLoadedClasses();
                retransformer.setClassMethodMatchers(this.pointCuts);
                final Set<Class<?>> classesToRetransform = InstrumentationContext.getMatchingClasses(retransformer.getMatchers(), allLoadedClasses);
                ReinstrumentUtils.checkClassExistsAndRetransformClasses(new ReinstrumentResult(), Collections.emptyList(), null, classesToRetransform);
            }
        }
    }
    
    private void reloadWeaveInstrumentationIfModified() {
        final File[] jarFiles = this.getExtensionFiles(ExtensionFileTypes.JAR.getFilter());
        final Collection<File> weaveFiles = Collections2.filter(Arrays.asList(jarFiles), new Predicate<File>() {
            public boolean apply(final File extension) {
                return JarExtension.isWeaveInstrumentation(extension);
            }
        });
        boolean fileModified = weaveFiles.size() != this.weaveElementCount;
        if (!fileModified) {
            for (final File file : weaveFiles) {
                fileModified |= (this.lastReloadedWeaveInstrumentation < file.lastModified());
            }
        }
        if (fileModified) {
            this.lastReloadedWeaveInstrumentation = System.currentTimeMillis();
            this.weaveElementCount = weaveFiles.size();
            this.weaveExtensions.clear();
            this.weaveExtensions.addAll(weaveFiles);
            final InstrumentationContextManager contextManager = ServiceFactory.getClassTransformerService().getContextManager();
            if (contextManager != null) {
                contextManager.getClassWeaverService().reloadInstrumentationPackages(this.weaveExtensions).run();
            }
            Agent.LOG.finer("Weave extension jars: " + this.weaveExtensions);
        }
    }
    
    private File[] getExtensionFiles(final FileFilter filter) {
        final File directory = this.getExtensionDirectory();
        if (directory == null) {
            return new File[0];
        }
        return directory.listFiles(filter);
    }
    
    private File getExtensionDirectory() {
        final AgentConfig agentConfig = ServiceFactory.getConfigService().getDefaultAgentConfig();
        String configDirName = agentConfig.getProperty("extensions.dir");
        if (configDirName == null) {
            configDirName = ConfigFileHelper.getNewRelicDirectory() + File.separator + "extensions";
        }
        File configDir = new File(configDirName);
        if (!configDir.exists()) {
            Agent.LOG.log(Level.FINE, "The extension directory " + configDir.getAbsolutePath() + " does not exist.");
            configDir = null;
        }
        else if (!configDir.isDirectory()) {
            Agent.LOG.log(Level.WARNING, "The extension directory " + configDir.getAbsolutePath() + " is not a directory.");
            configDir = null;
        }
        else if (!configDir.canRead()) {
            Agent.LOG.log(Level.WARNING, "The extension directory " + configDir.getAbsolutePath() + " is not readable.");
            configDir = null;
        }
        return configDir;
    }
    
    private void loadValidExtensions(final File[] files, final ExtensionParsers.ExtensionParser parser, final HashMap<String, Extension> extensions) {
        if (files != null) {
            for (final File file : files) {
                this.getLogger().log(Level.FINER, MessageFormat.format("Reading custom extension file {0}", file.getAbsolutePath()));
                try {
                    Extension currentExt = this.readExtension(parser, file);
                    currentExt = this.validateExtension(currentExt, extensions);
                    if (currentExt != null) {
                        extensions.put(currentExt.getName(), currentExt);
                    }
                    else {
                        this.getLogger().log(Level.WARNING, "Extension in file " + file.getAbsolutePath() + " could not be read in.");
                    }
                }
                catch (Exception ex) {
                    this.getLogger().severe("Unable to parse extension " + file.getAbsolutePath() + ".  " + ex.toString());
                    this.getLogger().log(Level.FINE, ex.toString(), ex);
                }
            }
        }
    }
    
    private Extension readExtension(final ExtensionParsers.ExtensionParser parser, final File file) throws Exception {
        final FileInputStream iStream = new FileInputStream(file);
        try {
            final Extension parse = parser.parse(ClassLoader.getSystemClassLoader(), iStream, true);
            iStream.close();
            return parse;
        }
        finally {
            iStream.close();
        }
    }
    
    protected Extension validateExtension(final Extension extension, final Map<String, Extension> existingExtensions) {
        final String name = extension.getName();
        if (name != null && name.length() != 0) {
            final double version = extension.getVersionNumber();
            final Extension existing = existingExtensions.get(name);
            if (existing == null) {
                this.getLogger().log(Level.FINER, MessageFormat.format("Adding extension with name {0} and version {1}", name, Double.valueOf(version).toString()));
                return extension;
            }
            if (version > existing.getVersionNumber()) {
                this.getLogger().log(Level.FINER, MessageFormat.format("Updating extension with name {0} to version {1}", name, Double.valueOf(version).toString()));
                return extension;
            }
            this.getLogger().log(Level.FINER, MessageFormat.format("Additional extension with name {0} and version {1} being ignored. Another file with name and version already read in.", name, Double.valueOf(version).toString()));
        }
        return null;
    }
    
    private void noticeExtensionClass(final Class<?> clazz) {
        this.getLogger().finest(MessageFormat.format("Noticed extension class {0}", clazz.getName()));
        if (Service.class.isAssignableFrom(clazz)) {
            try {
                this.addService((Service)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]));
            }
            catch (Exception ex) {
                this.getLogger().severe(MessageFormat.format("Unable to instantiate extension service \"{0}\"", clazz.getName()));
                this.getLogger().log(Level.FINE, "Unable to instantiate service", ex);
            }
        }
    }
    
    private void addService(final Service service) {
        String msg = MessageFormat.format("Noticed extension service \"{0}\"", service.getName());
        this.getLogger().finest(msg);
        if (!service.isEnabled()) {
            return;
        }
        this.services.add(service);
        msg = MessageFormat.format("Starting extension service \"{0}\"", service.getName());
        this.getLogger().finest(msg);
        try {
            service.start();
        }
        catch (Exception e) {
            msg = MessageFormat.format("Unable to start extension service \"{0}\" - {1}", service.getName(), e.toString());
            this.getLogger().severe(msg);
            this.getLogger().log(Level.FINE, msg, e);
        }
    }
    
    private Collection<JarExtension> loadJarExtensions(final File jarDirectory) {
        if (jarDirectory == null || !jarDirectory.exists()) {
            return (Collection<JarExtension>)Collections.emptyList();
        }
        if (jarDirectory.isDirectory()) {
            return this.loadJars(jarDirectory.listFiles(ExtensionFileTypes.JAR.getFilter()));
        }
        if (jarDirectory.exists()) {
            return this.loadJars(new File[] { jarDirectory });
        }
        return (Collection<JarExtension>)Collections.emptyList();
    }
    
    private Collection<JarExtension> loadJars(final File[] jarFiles) {
        final Collection<JarExtension> extensions = new ArrayList<JarExtension>();
        for (final File file : jarFiles) {
            try {
                final JarExtension ext = JarExtension.create(this.getLogger(), this.extensionParsers, file);
                extensions.add(ext);
            }
            catch (IOException ex) {
                Agent.LOG.severe("Unable to load extension " + file.getName());
                Agent.LOG.log(Level.FINER, ex.toString(), ex);
            }
        }
        return Collections.unmodifiableCollection((Collection<? extends JarExtension>)extensions);
    }
    
    public final List<ExtensionClassAndMethodMatcher> getEnabledPointCuts() {
        return this.pointCuts;
    }
    
    public void addConstruct(final ConfigurationConstruct construct) {
        this.constructs.add(construct);
    }
    
    public final Map<String, Extension> getInternalExtensions() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends Extension>)this.internalExtensions);
    }
    
    public final Set<Extension> getExtensions() {
        return this.extensions;
    }
    
    public Collection<File> getWeaveExtensions() {
        return this.weaveExtensions;
    }
}
