// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.api.agent;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.dispatchers.WebRequestDispatcher;
import com.newrelic.agent.dispatchers.Dispatcher;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.Transaction;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Collections;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.errors.ErrorService;
import java.util.Map;
import com.newrelic.agent.attributes.CustomAttributeSender;
import com.newrelic.agent.attributes.AttributeSender;
import com.newrelic.agent.bridge.PublicApi;

public class NewRelicApiImplementation implements PublicApi
{
    private final AttributeSender attributeSender;
    
    public NewRelicApiImplementation() {
        this.attributeSender = new CustomAttributeSender();
    }
    
    public void noticeError(final Throwable throwable, final Map<String, String> params) {
        try {
            ErrorService.reportException(throwable, filtorErrorAtts(params, this.attributeSender));
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Reported error: {0}", throwable);
                Agent.LOG.finer(msg);
            }
        }
        catch (Throwable t) {
            final String msg2 = MessageFormat.format("Exception reporting exception \"{0}\": {1}", throwable, t);
            logException(msg2, t);
        }
    }
    
    public void noticeError(final Throwable throwable) {
        final Map<String, String> params = Collections.emptyMap();
        this.noticeError(throwable, params);
    }
    
    public void noticeError(final String message, final Map<String, String> params) {
        try {
            ErrorService.reportError(message, filtorErrorAtts(params, this.attributeSender));
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Reported error: {0}", message);
                Agent.LOG.finer(msg);
            }
        }
        catch (Throwable t) {
            final String msg2 = MessageFormat.format("Exception reporting exception \"{0}\": {1}", message, t);
            logException(msg2, t);
        }
    }
    
    private static Map<String, String> filtorErrorAtts(final Map<String, String> params, final AttributeSender attributeSender) {
        final Map<String, String> atts = new TreeMap<String, String>();
        if (params != null) {
            final int maxErrorCount = getNumberOfErrorAttsLeft();
            for (final Map.Entry<String, String> current : params.entrySet()) {
                if (atts.size() >= maxErrorCount) {
                    Agent.LOG.log(Level.FINER, "Unable to add custom attribute for key \"{0}\" because the limit on error attributes has been reached.", new Object[] { current.getKey() });
                }
                else {
                    final Object value = attributeSender.verifyParameterAndReturnValue(current.getKey(), current.getValue(), "noticeError");
                    if (value == null) {
                        continue;
                    }
                    atts.put(current.getKey(), (String)value);
                }
            }
        }
        return atts;
    }
    
    private static int getNumberOfErrorAttsLeft() {
        final Transaction tx = Transaction.getTransaction();
        return tx.getAgentConfig().getMaxUserParameters() - tx.getErrorAttributes().size();
    }
    
    public void noticeError(final String message) {
        final Map<String, String> params = Collections.emptyMap();
        this.noticeError(message, params);
    }
    
    public void addCustomParameter(final String key, final String value) {
        this.attributeSender.addAttribute(key, value, "addCustomParameter");
    }
    
    public void addCustomParameter(final String key, final Number value) {
        this.attributeSender.addAttribute(key, value, "addCustomParameter");
    }
    
    public void setTransactionName(String category, String name) {
        if (Strings.isEmpty(category)) {
            category = "Custom";
        }
        if (name == null || name.length() == 0) {
            Agent.LOG.log(Level.FINER, "Unable to set the transaction name to an empty string");
            return;
        }
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        final Dispatcher dispatcher = tx.getDispatcher();
        if (dispatcher == null) {
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.finer(MessageFormat.format("Unable to set the transaction name to \"{0}\" in NewRelic API - no transaction", name));
            }
            return;
        }
        final boolean isWebTransaction = dispatcher.isWebTransaction();
        final TransactionNamingPolicy policy = TransactionNamingPolicy.getSameOrHigherPriorityTransactionNamingPolicy();
        final TransactionNamePriority namePriority = "Uri".equals(category) ? TransactionNamePriority.REQUEST_URI : TransactionNamePriority.CUSTOM_HIGH;
        if (Agent.LOG.isLoggable(Level.FINER)) {
            if (policy.canSetTransactionName(tx, namePriority)) {
                final String msg = MessageFormat.format("Setting {1} transaction name to \"{0}\" in NewRelic API", name, isWebTransaction ? "web" : "background");
                Agent.LOG.finer(msg);
            }
            else {
                Agent.LOG.finer("Unable to set the transaction name to " + name);
            }
        }
        synchronized (tx) {
            policy.setTransactionName(tx, name, category, namePriority);
        }
    }
    
    public void ignoreTransaction() {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        synchronized (tx) {
            tx.setIgnore(true);
        }
        if (Agent.LOG.isLoggable(Level.FINER)) {
            Agent.LOG.finer("Set ignore transaction in NewRelic API");
        }
    }
    
    public void ignoreApdex() {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        synchronized (tx) {
            tx.ignoreApdex();
        }
        if (Agent.LOG.isLoggable(Level.FINER)) {
            Agent.LOG.finer("Set ignore APDEX in NewRelic API");
        }
    }
    
    public void setRequestAndResponse(final Request request, final Response response) {
        final Transaction tx = Transaction.getTransaction();
        final Dispatcher dispatcher = new WebRequestDispatcher(request, response, tx);
        tx.setDispatcher(dispatcher);
        Agent.LOG.finest("Custom request dispatcher registered");
    }
    
    public static String getBrowserTimingHeaderForContentType(final String contentType) {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        try {
            if (!tx.isStarted()) {
                Agent.LOG.finer("Unable to inject browser timing header in a JSP: not running in a transaction");
                return "";
            }
            String header = null;
            synchronized (tx) {
                header = tx.getBrowserTransactionState().getBrowserTimingHeaderForJsp();
            }
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Injecting browser timing header in a JSP: {0}", header);
                Agent.LOG.log(Level.FINER, msg);
            }
            return header;
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("Error injecting browser timing header in a JSP: {0}", t);
            logException(msg, t);
            return "";
        }
    }
    
    public String getBrowserTimingHeader() {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        try {
            if (!tx.isStarted()) {
                Agent.LOG.finer("Unable to get browser timing header in NewRelic API: not running in a transaction");
                return "";
            }
            String header = null;
            synchronized (tx) {
                header = tx.getBrowserTransactionState().getBrowserTimingHeader();
            }
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Got browser timing header in NewRelic API: {0}", header);
                Agent.LOG.log(Level.FINER, msg);
            }
            return header;
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("Error getting browser timing header in NewRelic API: {0}", t);
            logException(msg, t);
            return "";
        }
    }
    
    public static String getBrowserTimingFooterForContentType(final String contentType) {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        try {
            if (!tx.isStarted()) {
                Agent.LOG.finer("Unable to inject browser timing footer in a JSP: not running in a transaction");
                return "";
            }
            String footer = null;
            synchronized (tx) {
                footer = tx.getBrowserTransactionState().getBrowserTimingFooter();
            }
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Injecting browser timing footer in a JSP: {0}", footer);
                Agent.LOG.log(Level.FINER, msg);
            }
            return footer;
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("Error injecting browser timing footer in a JSP: {0}", t);
            logException(msg, t);
            return "";
        }
    }
    
    public String getBrowserTimingFooter() {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        try {
            if (!tx.isStarted()) {
                Agent.LOG.finer("Unable to get browser timing footer in NewRelic API: not running in a transaction");
                return "";
            }
            String footer = null;
            synchronized (tx) {
                footer = tx.getBrowserTransactionState().getBrowserTimingFooter();
            }
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Got browser timing footer in NewRelic API: {0}", footer);
                Agent.LOG.log(Level.FINER, msg);
            }
            return footer;
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("Error getting browser timing footer in NewRelic API: {0}", t);
            logException(msg, t);
            return "";
        }
    }
    
    public void setUserName(final String name) {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        final Dispatcher dispatcher = tx.getDispatcher();
        if (dispatcher == null) {
            Agent.LOG.finer(MessageFormat.format("Unable to set the user name to \"{0}\" in NewRelic API - no transaction", name));
            return;
        }
        if (!dispatcher.isWebTransaction()) {
            Agent.LOG.finer(MessageFormat.format("Unable to set the user name to \"{0}\" in NewRelic API - transaction is not a web transaction", name));
            return;
        }
        if (Agent.LOG.isLoggable(Level.FINER)) {
            final String msg = MessageFormat.format("Attepmting to set user name to \"{0}\" in NewRelic API", name);
            Agent.LOG.finer(msg);
        }
        this.attributeSender.addAttribute("user", name, "setUserName");
    }
    
    public void setAccountName(final String name) {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        final Dispatcher dispatcher = tx.getDispatcher();
        if (dispatcher == null) {
            Agent.LOG.finer(MessageFormat.format("Unable to set the account name to \"{0}\" in NewRelic API - no transaction", name));
            return;
        }
        if (!dispatcher.isWebTransaction()) {
            Agent.LOG.finer(MessageFormat.format("Unable to set the account name to \"{0}\" in NewRelic API - transaction is not a web transaction", name));
            return;
        }
        if (Agent.LOG.isLoggable(Level.FINER)) {
            final String msg = MessageFormat.format("Attepmting to set account name to \"{0}\" in NewRelic API", name);
            Agent.LOG.finer(msg);
        }
        this.attributeSender.addAttribute("account", name, "setAccountName");
    }
    
    public void setProductName(final String name) {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        final Dispatcher dispatcher = tx.getDispatcher();
        if (dispatcher == null) {
            Agent.LOG.finer(MessageFormat.format("Unable to set the product name to \"{0}\" in NewRelic API - no transaction", name));
            return;
        }
        if (!dispatcher.isWebTransaction()) {
            Agent.LOG.finer(MessageFormat.format("Unable to set the product name to \"{0}\" in NewRelic API - transaction is not a web transaction", name));
            return;
        }
        if (Agent.LOG.isLoggable(Level.FINER)) {
            final String msg = MessageFormat.format("Attepmting to set product name to \"{0}\" in NewRelic API", name);
            Agent.LOG.finer(msg);
        }
        this.attributeSender.addAttribute("product", name, "setProductName");
    }
    
    private static void logException(final String msg, final Throwable t) {
        if (Agent.LOG.isLoggable(Level.FINEST)) {
            Agent.LOG.log(Level.FINEST, msg, t);
        }
        else if (Agent.LOG.isLoggable(Level.FINER)) {
            Agent.LOG.finer(msg);
        }
    }
    
    public static void initialize() {
        AgentBridge.publicApi = (PublicApi)new NewRelicApiImplementation();
    }
}
