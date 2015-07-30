// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.verifier;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.HashSet;
import java.io.InputStream;
import java.net.URL;
import java.lang.instrument.Instrumentation;
import com.newrelic.agent.instrumentation.verifier.mocks.MockInstrumentation;
import com.newrelic.agent.instrumentation.weaver.InstrumentationMetadata;
import java.util.jar.JarInputStream;
import java.io.File;
import com.newrelic.agent.instrumentation.weaver.Verifier;
import com.newrelic.agent.instrumentation.weaver.InstrumentationPackage;
import java.util.List;
import com.newrelic.agent.stats.StatsService;
import com.newrelic.agent.instrumentation.verifier.mocks.MockStatsService;
import com.newrelic.agent.service.ServiceManager;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.verifier.mocks.MockServiceManager;
import com.newrelic.agent.logging.IAgentLogger;

public class InstrumentationVerifier
{
    private IAgentLogger logger;
    
    public InstrumentationVerifier(final IAgentLogger logger) {
        this.logger = logger;
        this.setUpDummyServices();
    }
    
    private void setUpDummyServices() {
        final MockServiceManager myServiceManager = new MockServiceManager();
        ServiceFactory.setServiceManager(myServiceManager);
        myServiceManager.setStatsService(new MockStatsService());
    }
    
    public boolean verify(final String instrumentationJar, final List<String> userJars) throws Exception {
        final ClassLoader loader = this.createClassloaderForVerification(userJars);
        final InstrumentationPackage instrumentationPackage = this.getInstrumentationPackage(instrumentationJar);
        final Verifier verifier = instrumentationPackage.getVerifier();
        final boolean result = verifier.verify(instrumentationPackage.getClassAppender(), loader, instrumentationPackage.getClassBytes(), instrumentationPackage.newClassLoadOrder);
        return result;
    }
    
    private InstrumentationPackage getInstrumentationPackage(final String instrumentationJar) throws Exception {
        final URL instrumentationJarUrl = new File(instrumentationJar).toURI().toURL();
        final InputStream iStream = instrumentationJarUrl.openStream();
        final JarInputStream jarStream = new JarInputStream(iStream);
        final InstrumentationMetadata instrumentationMetadata = new InstrumentationMetadata(jarStream, instrumentationJarUrl.toString());
        return new InstrumentationPackage(new MockInstrumentation(), this.logger, instrumentationMetadata, jarStream);
    }
    
    private ClassLoader createClassloaderForVerification(final List<String> jars) throws MalformedURLException {
        final Set<URL> urls = new HashSet<URL>();
        this.logger.log(Level.FINE, "Creating user classloader with custom classpath:");
        for (final String s : jars) {
            final File jarFile = new File(s);
            if (!jarFile.exists()) {
                this.logger.log(Level.WARNING, "\tWARNING: Given jar does not exist: {0}", new Object[] { s });
            }
            urls.add(jarFile.toURI().toURL());
            this.logger.log(Level.FINE, "\t{0}", new Object[] { s });
        }
        return new VerificationClassLoader(urls.toArray(new URL[urls.size()]));
    }
    
    public void printVerificationResults(final PrintStream ps, final List<String> results) {
        for (final String logLine : results) {
            ps.println(logLine);
        }
    }
}
