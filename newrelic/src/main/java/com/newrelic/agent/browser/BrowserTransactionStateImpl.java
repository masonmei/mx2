// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.browser;

import com.newrelic.agent.attributes.AttributesUtils;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.dispatchers.Dispatcher;
import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.ITransaction;

public class BrowserTransactionStateImpl implements BrowserTransactionState
{
    private final Object lock;
    private final ITransaction tx;
    private boolean browserHeaderRendered;
    private boolean browserFooterRendered;
    
    protected BrowserTransactionStateImpl(final ITransaction tx) {
        this.lock = new Object();
        this.tx = tx;
    }
    
    public String getBrowserTimingHeaderForJsp() {
        synchronized (this.lock) {
            if (!this.canRenderHeaderForJsp()) {
                return "";
            }
            return this.getBrowserTimingHeader2();
        }
    }
    
    public String getBrowserTimingHeader() {
        synchronized (this.lock) {
            if (!this.canRenderHeader()) {
                return "";
            }
            return this.getBrowserTimingHeader2();
        }
    }
    
    private String getBrowserTimingHeader2() {
        final IBrowserConfig config = this.getBeaconConfig();
        if (config == null) {
            Agent.LOG.finer("Real user monitoring is disabled");
            return "";
        }
        final String header = config.getBrowserTimingHeader();
        this.browserHeaderRendered = true;
        return header;
    }
    
    public String getBrowserTimingFooter() {
        synchronized (this.lock) {
            if (!this.canRenderFooter()) {
                return "";
            }
            return this.getBrowserTimingFooter2();
        }
    }
    
    private String getBrowserTimingFooter2() {
        final IBrowserConfig config = this.getBeaconConfig();
        if (config == null) {
            Agent.LOG.finer("Real user monitoring is disabled");
            return "";
        }
        this.tx.freezeTransactionName();
        if (this.tx.isIgnore()) {
            Agent.LOG.finer("Unable to get browser timing footer: transaction is ignore");
            return "";
        }
        final String footer = config.getBrowserTimingFooter(this);
        if (!footer.isEmpty()) {
            this.browserFooterRendered = true;
        }
        return footer;
    }
    
    private boolean canRenderHeader() {
        if (!this.tx.isInProgress()) {
            Agent.LOG.finer("Unable to get browser timing header: transaction has no tracers");
            return false;
        }
        if (this.tx.isIgnore()) {
            Agent.LOG.finer("Unable to get browser timing header: transaction is ignore");
            return false;
        }
        if (this.browserHeaderRendered) {
            Agent.LOG.finer("browser timing header already rendered");
            return false;
        }
        return true;
    }
    
    private boolean canRenderHeaderForJsp() {
        if (!this.canRenderHeader()) {
            return false;
        }
        final Dispatcher dispatcher = this.tx.getDispatcher();
        if (dispatcher == null || !dispatcher.isWebTransaction()) {
            Agent.LOG.finer("Unable to get browser timing header: transaction is not a web transaction");
            return false;
        }
        try {
            final String contentType = dispatcher.getResponse().getContentType();
            if (!this.isHtml(contentType)) {
                final String msg = MessageFormat.format("Unable to inject browser timing header in a JSP: bad content type: {0}", contentType);
                Agent.LOG.finer(msg);
                return false;
            }
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Unable to inject browser timing header in a JSP: exception getting content type: {0}", e);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.FINEST, msg, e);
            }
            else if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.finer(msg);
            }
            return false;
        }
        return true;
    }
    
    private boolean isHtml(final String contentType) {
        return contentType != null && (contentType.startsWith("text/html") || contentType.startsWith("text/xhtml"));
    }
    
    private boolean canRenderFooter() {
        if (!this.tx.isInProgress()) {
            Agent.LOG.finer("Unable to get browser timing footer: transaction has no tracers");
            return false;
        }
        if (this.tx.isIgnore()) {
            Agent.LOG.finer("Unable to get browser timing footer: transaction is ignore");
            return false;
        }
        if (this.browserFooterRendered && !this.tx.getAgentConfig().getBrowserMonitoringConfig().isAllowMultipleFooters()) {
            Agent.LOG.finer("browser timing footer already rendered");
            return false;
        }
        if (this.browserHeaderRendered) {
            return true;
        }
        final IBrowserConfig config = this.getBeaconConfig();
        if (config == null) {
            Agent.LOG.finer("Real user monitoring is disabled");
            return false;
        }
        Agent.LOG.finer("getBrowserTimingFooter() was invoked without a call to getBrowserTimingHeader()");
        return false;
    }
    
    protected IBrowserConfig getBeaconConfig() {
        final String appName = this.tx.getApplicationName();
        return ServiceFactory.getBeaconService().getBrowserConfig(appName);
    }
    
    public long getDurationInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.tx.getRunningDurationInNanos(), TimeUnit.NANOSECONDS);
    }
    
    public long getExternalTimeInMilliseconds() {
        return this.tx.getExternalTime();
    }
    
    public String getTransactionName() {
        return this.tx.getPriorityTransactionName().getName();
    }
    
    public static BrowserTransactionState create(final ITransaction tx) {
        return (tx == null) ? null : new BrowserTransactionStateImpl(tx);
    }
    
    public Map<String, Object> getUserAttributes() {
        return this.tx.getUserAttributes();
    }
    
    public Map<String, Object> getAgentAttributes() {
        synchronized (this.lock) {
            final Map<String, Object> atts = (Map<String, Object>)Maps.newHashMap();
            atts.putAll(this.tx.getAgentAttributes());
            atts.putAll(AttributesUtils.appendAttributePrefixes(this.tx.getPrefixedAgentAttributes()));
            return atts;
        }
    }
    
    public String getAppName() {
        return this.tx.getApplicationName();
    }
}
