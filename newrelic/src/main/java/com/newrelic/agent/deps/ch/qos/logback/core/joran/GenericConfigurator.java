// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusChecker;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEventRecorder;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.SimpleRuleStore;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Pattern;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.RuleStore;
import org.xml.sax.InputSource;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.io.FileInputStream;
import java.io.File;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.IOException;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.JoranException;
import java.net.URL;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Interpreter;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public abstract class GenericConfigurator extends ContextAwareBase
{
    protected Interpreter interpreter;
    
    public final void doConfigure(final URL url) throws JoranException {
        InputStream in = null;
        try {
            informContextOfURLUsedForConfiguration(this.getContext(), url);
            final URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            in = urlConnection.getInputStream();
            this.doConfigure(in);
        }
        catch (IOException ioe) {
            final String errMsg = "Could not open URL [" + url + "].";
            this.addError(errMsg, ioe);
            throw new JoranException(errMsg, ioe);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ioe2) {
                    final String errMsg2 = "Could not close input stream";
                    this.addError(errMsg2, ioe2);
                    throw new JoranException(errMsg2, ioe2);
                }
            }
        }
    }
    
    public final void doConfigure(final String filename) throws JoranException {
        this.doConfigure(new File(filename));
    }
    
    public final void doConfigure(final File file) throws JoranException {
        FileInputStream fis = null;
        try {
            informContextOfURLUsedForConfiguration(this.getContext(), file.toURI().toURL());
            fis = new FileInputStream(file);
            this.doConfigure(fis);
        }
        catch (IOException ioe) {
            final String errMsg = "Could not open [" + file.getPath() + "].";
            this.addError(errMsg, ioe);
            throw new JoranException(errMsg, ioe);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException ioe2) {
                    final String errMsg2 = "Could not close [" + file.getName() + "].";
                    this.addError(errMsg2, ioe2);
                    throw new JoranException(errMsg2, ioe2);
                }
            }
        }
    }
    
    public static void informContextOfURLUsedForConfiguration(final Context context, final URL url) {
        ConfigurationWatchListUtil.setMainWatchURL(context, url);
    }
    
    public final void doConfigure(final InputStream inputStream) throws JoranException {
        this.doConfigure(new InputSource(inputStream));
    }
    
    protected abstract void addInstanceRules(final RuleStore p0);
    
    protected abstract void addImplicitRules(final Interpreter p0);
    
    protected void addDefaultNestedComponentRegistryRules(final DefaultNestedComponentRegistry registry) {
    }
    
    protected Pattern initialPattern() {
        return new Pattern();
    }
    
    protected void buildInterpreter() {
        final RuleStore rs = new SimpleRuleStore(this.context);
        this.addInstanceRules(rs);
        this.interpreter = new Interpreter(this.context, rs, this.initialPattern());
        final InterpretationContext interpretationContext = this.interpreter.getInterpretationContext();
        interpretationContext.setContext(this.context);
        this.addImplicitRules(this.interpreter);
        this.addDefaultNestedComponentRegistryRules(interpretationContext.getDefaultNestedComponentRegistry());
    }
    
    public final void doConfigure(final InputSource inputSource) throws JoranException {
        final long threshold = System.currentTimeMillis();
        if (!ConfigurationWatchListUtil.wasConfigurationWatchListReset(this.context)) {
            informContextOfURLUsedForConfiguration(this.getContext(), null);
        }
        final SaxEventRecorder recorder = new SaxEventRecorder();
        recorder.setContext(this.context);
        recorder.recordEvents(inputSource);
        this.doConfigure(recorder.saxEventList);
        final StatusChecker statusChecker = new StatusChecker(this.context);
        if (statusChecker.noXMLParsingErrorsOccurred(threshold)) {
            this.addInfo("Registering current configuration as safe fallback point");
            this.registerSafeConfiguration();
        }
    }
    
    public void doConfigure(final List<SaxEvent> eventList) throws JoranException {
        this.buildInterpreter();
        synchronized (this.context.getConfigurationLock()) {
            this.interpreter.getEventPlayer().play(eventList);
        }
    }
    
    public void registerSafeConfiguration() {
        this.context.putObject("SAFE_JORAN_CONFIGURATION", this.interpreter.getEventPlayer().getCopyOfPlayerEventList());
    }
    
    public List<SaxEvent> recallSafeConfiguration() {
        return (List<SaxEvent>)this.context.getObject("SAFE_JORAN_CONFIGURATION");
    }
}
