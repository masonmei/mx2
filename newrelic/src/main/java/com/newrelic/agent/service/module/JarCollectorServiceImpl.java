// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.module;

import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.config.JarCollectorConfig;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.AbstractService;

public class JarCollectorServiceImpl extends AbstractService implements JarCollectorService, HarvestListener
{
    private final JarCollectorServiceProcessor processor;
    private long lastAllJarFlush;
    private final String defaultApp;
    private final boolean enabled;
    private final AtomicReference<Map<String, URL>> queuedJars;
    
    private Map<String, URL> newUrlMap() {
        return Maps.newConcurrentMap();
    }
    
    public JarCollectorServiceImpl() {
        super(JarCollectorService.class.getSimpleName());
        this.processor = new JarCollectorServiceProcessor();
        this.lastAllJarFlush = 0L;
        this.queuedJars = new AtomicReference<Map<String, URL>>(this.newUrlMap());
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.defaultApp = config.getApplicationName();
        final JarCollectorConfig jarCollectorConfig = config.getJarCollectorConfig();
        this.enabled = jarCollectorConfig.isEnabled();
    }
    
    public final boolean isEnabled() {
        return this.enabled;
    }
    
    protected void doStart() throws Exception {
        if (this.enabled) {
            ServiceFactory.getHarvestService().addHarvestListener(this);
        }
    }
    
    protected void doStop() throws Exception {
        ServiceFactory.getHarvestService().removeHarvestListener(this);
    }
    
    private boolean needToSendAllJars() {
        return ServiceFactory.getRPMService().getConnectionTimestamp() > this.lastAllJarFlush;
    }
    
    public synchronized void beforeHarvest(final String pAppName, final StatsEngine pStatsEngine) {
        if (!this.defaultApp.equals(pAppName)) {
            return;
        }
        Agent.LOG.log(Level.FINER, "Harvesting Modules");
        final boolean sendAll = this.needToSendAllJars();
        final Map<String, URL> urls = this.queuedJars.getAndSet(this.newUrlMap());
        final List<Jar> jars = this.processor.processModuleData(urls.values(), sendAll);
        if (sendAll) {
            this.lastAllJarFlush = System.nanoTime();
        }
        if (Agent.LOG.isLoggable(Level.FINEST)) {
            final StringBuilder sb = new StringBuilder();
            for (final Jar jar : jars) {
                sb.append("   ");
                sb.append(jar.getName());
                sb.append(":");
                sb.append(jar.getVersion());
            }
            Agent.LOG.log(Level.FINEST, "Sending jars: " + sb.toString());
        }
        if (!jars.isEmpty()) {
            try {
                ServiceFactory.getRPMService(pAppName).sendModules(jars);
            }
            catch (Exception e) {
                Agent.LOG.log(Level.FINE, MessageFormat.format("Unable to send {0} jar(s). Will attempt next harvest.", jars.size()));
                this.queuedJars.get().putAll(urls);
            }
        }
    }
    
    public void afterHarvest(final String pAppName) {
    }
    
    Map<String, URL> getQueuedJars() {
        return this.queuedJars.get();
    }
    
    void addUrls(final URL... urls) {
        if (this.enabled) {
            for (final URL url : urls) {
                if ("jar".equals(url.getProtocol())) {
                    String path = url.getFile();
                    if (!this.queuedJars.get().containsKey(path)) {
                        final int index = path.lastIndexOf(".jar");
                        if (index > 0) {
                            path = path.substring(0, index + ".jar".length());
                        }
                        try {
                            final URL newUrl = new URL(path);
                            this.queuedJars.get().put(url.getPath(), newUrl);
                        }
                        catch (MalformedURLException e) {
                            Agent.LOG.log(Level.FINEST, (Throwable)e, "Error parsing jar: {0}", new Object[] { e.getMessage() });
                        }
                    }
                }
                else if (url.getFile().endsWith(".jar")) {
                    this.queuedJars.get().put(url.getFile(), url);
                }
                else {
                    final int jarIndex = url.getFile().lastIndexOf(".jar");
                    if (jarIndex > 0) {
                        final String path2 = url.getFile().substring(0, jarIndex + ".jar".length());
                        if (!this.queuedJars.get().containsKey(path2)) {
                            try {
                                final URL newUrl = new URL(url.getProtocol(), url.getHost(), path2);
                                this.queuedJars.get().put(path2, newUrl);
                            }
                            catch (MalformedURLException e) {
                                Agent.LOG.log(Level.FINEST, (Throwable)e, "Error parsing jar: {0}", new Object[] { e.getMessage() });
                            }
                        }
                    }
                }
            }
        }
    }
    
    public ClassMatchVisitorFactory getSourceVisitor() {
        return new ClassMatchVisitorFactory() {
            public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, final ClassVisitor cv, final InstrumentationContext context) {
                if (JarCollectorServiceImpl.this.enabled && null != context.getProtectionDomain() && null != context.getProtectionDomain().getCodeSource() && null != context.getProtectionDomain().getCodeSource().getLocation()) {
                    JarCollectorServiceImpl.this.addUrls(context.getProtectionDomain().getCodeSource().getLocation());
                }
                return null;
            }
        };
    }
}
