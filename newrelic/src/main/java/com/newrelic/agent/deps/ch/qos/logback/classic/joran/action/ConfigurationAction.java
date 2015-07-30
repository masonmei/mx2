// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.classic.turbo.TurboFilter;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import com.newrelic.agent.deps.ch.qos.logback.core.util.Duration;
import com.newrelic.agent.deps.ch.qos.logback.classic.turbo.ReconfigureOnChangeFilter;
import com.newrelic.agent.deps.ch.qos.logback.core.util.ContextUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.status.OnConsoleStatusListener;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public class ConfigurationAction extends Action
{
    static final String INTERNAL_DEBUG_ATTR = "debug";
    static final String SCAN_ATTR = "scan";
    static final String SCAN_PERIOD_ATTR = "scanPeriod";
    static final String DEBUG_SYSTEM_PROPERTY_KEY = "logback.debug";
    long threshold;
    
    public ConfigurationAction() {
        this.threshold = 0L;
    }
    
    public void begin(final InterpretationContext ic, final String name, final Attributes attributes) {
        this.threshold = System.currentTimeMillis();
        String debugAttrib = System.getProperty("logback.debug");
        if (debugAttrib == null) {
            debugAttrib = ic.subst(attributes.getValue("debug"));
        }
        if (OptionHelper.isEmpty(debugAttrib) || debugAttrib.equalsIgnoreCase("false") || debugAttrib.equalsIgnoreCase("null")) {
            this.addInfo("debug attribute not set");
        }
        else {
            OnConsoleStatusListener.addNewInstanceToContext(this.context);
        }
        this.processScanAttrib(ic, attributes);
        new ContextUtil(this.context).addHostNameAsProperty();
        ic.pushObject(this.getContext());
    }
    
    void processScanAttrib(final InterpretationContext ic, final Attributes attributes) {
        final String scanAttrib = ic.subst(attributes.getValue("scan"));
        if (!OptionHelper.isEmpty(scanAttrib) && !"false".equalsIgnoreCase(scanAttrib)) {
            final ReconfigureOnChangeFilter rocf = new ReconfigureOnChangeFilter();
            rocf.setContext(this.context);
            final String scanPeriodAttrib = ic.subst(attributes.getValue("scanPeriod"));
            if (!OptionHelper.isEmpty(scanPeriodAttrib)) {
                try {
                    final Duration duration = Duration.valueOf(scanPeriodAttrib);
                    rocf.setRefreshPeriod(duration.getMilliseconds());
                    this.addInfo("Setting ReconfigureOnChangeFilter scanning period to " + duration);
                }
                catch (NumberFormatException nfe) {
                    this.addError("Error while converting [" + scanAttrib + "] to long", nfe);
                }
            }
            rocf.start();
            final LoggerContext lc = (LoggerContext)this.context;
            this.addInfo("Adding ReconfigureOnChangeFilter as a turbo filter");
            lc.addTurboFilter(rocf);
        }
    }
    
    public void end(final InterpretationContext ec, final String name) {
        this.addInfo("End of configuration.");
        ec.popObject();
    }
}
