// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.errors;

import java.util.HashSet;
import com.newrelic.agent.tracers.ClassMethodSignature;
import java.util.Iterator;
import com.newrelic.agent.instrumentation.methodmatchers.InvalidMethodDescriptor;
import com.newrelic.agent.instrumentation.yaml.PointCutFactory;
import com.newrelic.agent.instrumentation.PointCut;
import java.util.Collection;
import com.newrelic.agent.TransactionErrorPriority;
import com.newrelic.agent.Transaction;
import java.util.Map;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.TransactionData;
import java.util.Collections;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.IRPMService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.TransactionListener;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.config.StripExceptionConfig;
import com.newrelic.agent.config.ErrorCollectorConfig;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;

public class ErrorService
{
    public static final int ERROR_LIMIT_PER_REPORTING_PERIOD = 20;
    public static final String STRIPPED_EXCEPTION_REPLACEMENT = "Message removed by New Relic 'strip_exception_messages' setting";
    private static final Set<String> IGNORE_ERRORS;
    protected final AtomicInteger errorCountThisHarvest;
    private final AtomicInteger errorCount;
    private final AtomicLong totalErrorCount;
    private final AtomicReferenceArray<TracedError> tracedErrors;
    private volatile ErrorCollectorConfig errorCollectorConfig;
    private volatile StripExceptionConfig stripExceptionConfig;
    private final boolean shouldRecordErrorCount;
    private final String appName;
    
    public ErrorService(final String appName) {
        this.errorCountThisHarvest = new AtomicInteger();
        this.errorCount = new AtomicInteger();
        this.totalErrorCount = new AtomicLong();
        this.appName = appName;
        this.errorCollectorConfig = ServiceFactory.getConfigService().getErrorCollectorConfig(appName);
        this.stripExceptionConfig = ServiceFactory.getConfigService().getStripExceptionConfig(appName);
        this.tracedErrors = new AtomicReferenceArray<TracedError>(20);
        ServiceFactory.getTransactionService().addTransactionListener(new MyTransactionListener());
        ServiceFactory.getConfigService().addIAgentConfigListener(new MyConfigListener());
        this.shouldRecordErrorCount = !Boolean.getBoolean("com.newrelic.agent.errors.no_error_metric");
    }
    
    protected void refreshErrorCollectorConfig(final AgentConfig agentConfig) {
        final ErrorCollectorConfig oldErrorConfig = this.errorCollectorConfig;
        this.errorCollectorConfig = agentConfig.getErrorCollectorConfig();
        if (this.errorCollectorConfig.isEnabled() == oldErrorConfig.isEnabled()) {
            return;
        }
        final String msg = MessageFormat.format("Errors will{0} be sent to New Relic for {1}", this.errorCollectorConfig.isEnabled() ? "" : " not", this.appName);
        Agent.LOG.info(msg);
    }
    
    protected void refreshStripExceptionConfig(final AgentConfig agentConfig) {
        final StripExceptionConfig oldStripExceptionConfig = this.stripExceptionConfig;
        this.stripExceptionConfig = agentConfig.getStripExceptionConfig();
        if (this.stripExceptionConfig.isEnabled() != oldStripExceptionConfig.isEnabled()) {
            Agent.LOG.info(MessageFormat.format("Exception messages will{0} be stripped before sending to New Relic for {1}", this.stripExceptionConfig.isEnabled() ? "" : " not", this.appName));
        }
        if (!this.stripExceptionConfig.getWhitelist().equals(oldStripExceptionConfig.getWhitelist())) {
            Agent.LOG.info(MessageFormat.format("Exception message whitelist updated to {0} for {1}", this.stripExceptionConfig.getWhitelist().toString(), this.appName));
        }
    }
    
    public void reportError(final TracedError error) {
        if (error == null) {
            return;
        }
        if (error instanceof ThrowableError && this.isIgnoredError(200, ((ThrowableError)error).getThrowable())) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final Throwable throwable = ((ThrowableError)error).getThrowable();
                final String errorString = (throwable == null) ? "" : throwable.getClass().getName();
                final String msg = MessageFormat.format("Ignoring error {0} for {1}", errorString, this.appName);
                Agent.LOG.finer(msg);
            }
            return;
        }
        if (error.incrementsErrorMetric()) {
            this.errorCountThisHarvest.incrementAndGet();
        }
        if (!this.errorCollectorConfig.isEnabled()) {
            return;
        }
        if (this.errorCount.get() >= 20) {
            Agent.LOG.finer(MessageFormat.format("Error limit exceeded for {0}: {1}", this.appName, error));
            return;
        }
        final int index = (int)this.totalErrorCount.getAndIncrement() % 20;
        if (this.tracedErrors.compareAndSet(index, null, error)) {
            this.errorCount.getAndIncrement();
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg2 = MessageFormat.format("Recording error for {0} : {1}", this.appName, error);
                Agent.LOG.finer(msg2);
            }
        }
    }
    
    public void reportErrors(final TracedError... errors) {
        for (final TracedError error : errors) {
            this.reportError(error);
        }
    }
    
    public List<TracedError> getTracedErrors() {
        final List<TracedError> errors = new ArrayList<TracedError>(20);
        for (int i = 0; i < this.tracedErrors.length(); ++i) {
            final TracedError error = this.tracedErrors.getAndSet(i, null);
            if (error != null) {
                this.errorCount.getAndDecrement();
                errors.add(error);
            }
        }
        return errors;
    }
    
    public List<TracedError> harvest(final IRPMService rpmService, final StatsEngine statsEngine) {
        if (!this.errorCollectorConfig.isEnabled()) {
            return Collections.emptyList();
        }
        this.recordMetrics(statsEngine);
        if (rpmService.isConnected()) {
            return this.getTracedErrors();
        }
        return Collections.emptyList();
    }
    
    private void recordMetrics(final StatsEngine statsEngine) {
        final int errorCount = this.errorCountThisHarvest.getAndSet(0);
        if (this.shouldRecordErrorCount) {
            statsEngine.getStats("Errors/all").incrementCallCount(errorCount);
        }
    }
    
    private void noticeTransaction(final TransactionData td, final TransactionStats transactionStats) {
        if (!this.appName.equals(td.getApplicationName())) {
            return;
        }
        if (!this.errorCollectorConfig.isEnabled()) {
            return;
        }
        String statusMessage = td.getStatusMessage();
        final int responseStatus = td.getResponseStatus();
        Throwable throwable = td.getThrowable();
        final boolean isReportable = responseStatus >= 400 || throwable != null;
        if (throwable instanceof ReportableError) {
            statusMessage = throwable.getMessage();
            throwable = null;
        }
        if (isReportable) {
            if (this.isIgnoredError(td)) {
                if (Agent.LOG.isLoggable(Level.FINER)) {
                    final String errorString = (throwable == null) ? "" : throwable.getClass().getName();
                    final String msg = MessageFormat.format("Ignoring error {0} for {1} {2} ({3})", errorString, td.getRequestUri(), this.appName, responseStatus);
                    Agent.LOG.finer(msg);
                }
                return;
            }
            TracedError error;
            if (throwable != null) {
                error = new ThrowableError(this.appName, td.getBlameOrRootMetricName(), throwable, td.getRequestUri(), td.getWallClockStartTimeMs(), td.getPrefixedAttributes(), td.getUserAttributes(), td.getAgentAttributes(), td.getErrorAttributes(), td.getIntrinsicAttributes());
            }
            else {
                error = new HttpTracedError(this.appName, td.getBlameOrRootMetricName(), responseStatus, statusMessage, td.getRequestUri(), td.getStartTimeInNanos(), td.getPrefixedAttributes(), td.getUserAttributes(), td.getAgentAttributes(), td.getErrorAttributes(), td.getIntrinsicAttributes());
            }
            if (this.shouldRecordErrorCount && error.incrementsErrorMetric()) {
                this.recordErrorCount(td, transactionStats);
            }
            if (this.errorCount.get() < 20) {
                this.reportError(error);
            }
            else if (error.incrementsErrorMetric()) {
                this.errorCountThisHarvest.incrementAndGet();
            }
        }
    }
    
    private void recordErrorCount(final TransactionData td, final TransactionStats transactionStats) {
        final String metricName = this.getErrorCountMetricName(td);
        if (metricName != null) {
            transactionStats.getUnscopedStats().getStats(metricName).incrementCallCount();
        }
        final String metricNameAll = td.isWebTransaction() ? "Errors/allWeb" : "Errors/allOther";
        transactionStats.getUnscopedStats().getStats(metricNameAll).incrementCallCount();
    }
    
    private String getErrorCountMetricName(final TransactionData td) {
        final String blameMetricName = td.getBlameMetricName();
        if (blameMetricName != null) {
            final StringBuilder output = new StringBuilder("Errors/".length() + blameMetricName.length());
            output.append("Errors/");
            output.append(blameMetricName);
            return output.toString();
        }
        return null;
    }
    
    public boolean isIgnoredError(final int responseStatus, Throwable throwable) {
        if (this.errorCollectorConfig.getIgnoreStatusCodes().contains(responseStatus)) {
            return true;
        }
        while (throwable != null) {
            final String name = throwable.getClass().getName();
            if (this.errorCollectorConfig.getIgnoreErrors().contains(name)) {
                return true;
            }
            if (ErrorService.IGNORE_ERRORS.contains(name)) {
                return true;
            }
            throwable = throwable.getCause();
        }
        return false;
    }
    
    public boolean isIgnoredError(final TransactionData transactionData) {
        return this.isIgnoredError(transactionData.getResponseStatus(), transactionData.getThrowable());
    }
    
    public static void reportException(final Throwable throwable, final Map<String, String> params) {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        if (tx.isInProgress()) {
            if (params != null) {
                tx.getErrorAttributes().putAll(params);
            }
            synchronized (tx) {
                tx.setThrowable(throwable, TransactionErrorPriority.API);
            }
        }
        else {
            final String uri = '/' + Thread.currentThread().getName();
            final TracedError error = new ThrowableError(null, "OtherTransaction" + uri, throwable, uri, System.currentTimeMillis(), null, null, null, params, null);
            ServiceFactory.getRPMService().getErrorService().reportError(error);
        }
    }
    
    public static void reportError(final String message, final Map<String, String> params) {
        final Transaction tx = Transaction.getTransaction().getRootTransaction();
        if (tx.isInProgress()) {
            if (params != null) {
                tx.getErrorAttributes().putAll(params);
            }
            synchronized (tx) {
                tx.setThrowable(new ReportableError(message), TransactionErrorPriority.API);
            }
        }
        else {
            final String uri = '/' + Thread.currentThread().getName();
            final TracedError error = new HttpTracedError(null, "OtherTransaction" + uri, 500, message, uri, System.currentTimeMillis(), null, null, null, params, null);
            ServiceFactory.getRPMService().getErrorService().reportError(error);
        }
    }
    
    public static Collection<? extends PointCut> getEnabledErrorHandlerPointCuts() {
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        final Object exceptionHandlers = config.getErrorCollectorConfig().getProperty("exception_handlers");
        if (exceptionHandlers == null) {
            return Collections.emptyList();
        }
        final Collection<PointCut> pointcuts = new ArrayList<PointCut>();
        if (exceptionHandlers instanceof Collection) {
            for (final Object sigObject : (Collection)exceptionHandlers) {
                if (sigObject instanceof ExceptionHandlerSignature) {
                    final ExceptionHandlerSignature exHandlerSig = (ExceptionHandlerSignature)sigObject;
                    final String msg = MessageFormat.format("Instrumenting exception handler signature {0}", exHandlerSig.toString());
                    Agent.LOG.finer(msg);
                    final ExceptionHandlerPointCut pc = new ExceptionHandlerPointCut(exHandlerSig);
                    if (!pc.isEnabled()) {
                        continue;
                    }
                    pointcuts.add(pc);
                }
                else if (sigObject instanceof String) {
                    final ClassMethodSignature signature = PointCutFactory.parseClassMethodSignature(sigObject.toString());
                    try {
                        final ExceptionHandlerSignature exHandlerSig2 = new ExceptionHandlerSignature(signature);
                        Agent.LOG.info(MessageFormat.format("Instrumenting exception handler signature {0}", exHandlerSig2.toString()));
                        final ExceptionHandlerPointCut pc = new ExceptionHandlerPointCut(exHandlerSig2);
                        if (!pc.isEnabled()) {
                            continue;
                        }
                        pointcuts.add(pc);
                    }
                    catch (InvalidMethodDescriptor e) {
                        Agent.LOG.severe(MessageFormat.format("Unable to instrument exception handler {0} : {1}", sigObject.toString(), e.toString()));
                    }
                }
                else {
                    if (!(sigObject instanceof Exception)) {
                        continue;
                    }
                    Agent.LOG.severe(MessageFormat.format("Unable to instrument exception handler : {0}", sigObject.toString()));
                }
            }
        }
        return pointcuts;
    }
    
    public static void reportHTTPError(final String message, final int statusCode, final String uri) {
        final TracedError error = new HttpTracedError(null, "WebTransaction" + uri, statusCode, message, uri, System.currentTimeMillis(), null, null, null, null, null);
        ServiceFactory.getRPMService().getErrorService().reportError(error);
        Agent.LOG.finer(MessageFormat.format("Reported HTTP error {0} with status code {1} URI {2}", message, statusCode, uri));
    }
    
    public static String getStrippedExceptionMessage(final Throwable throwable) {
        final ErrorService errorService = ServiceFactory.getRPMService().getErrorService();
        if (errorService.stripExceptionConfig.isEnabled() && !errorService.stripExceptionConfig.getWhitelist().contains(throwable.getClass().getName())) {
            return "Message removed by New Relic 'strip_exception_messages' setting";
        }
        return throwable.getMessage();
    }
    
    static {
        final Set<String> ignoreErrors = new HashSet<String>(4);
        ignoreErrors.add("org.eclipse.jetty.continuation.ContinuationThrowable");
        ignoreErrors.add("org.mortbay.jetty.RetryRequest");
        IGNORE_ERRORS = Collections.unmodifiableSet((Set<? extends String>)ignoreErrors);
    }
    
    private static class ReportableError extends Throwable
    {
        private static final long serialVersionUID = 3472056044517410355L;
        
        public ReportableError(final String message) {
            super(message);
        }
    }
    
    private class MyTransactionListener implements TransactionListener
    {
        public void dispatcherTransactionFinished(final TransactionData transactionData, final TransactionStats transactionStats) {
            ErrorService.this.noticeTransaction(transactionData, transactionStats);
        }
    }
    
    private class MyConfigListener implements AgentConfigListener
    {
        public void configChanged(final String appName, final AgentConfig agentConfig) {
            if (ErrorService.this.appName.equals(appName)) {
                Agent.LOG.fine(MessageFormat.format("Error service received configuration change notification for {0}", appName));
                ErrorService.this.refreshErrorCollectorConfig(agentConfig);
                ErrorService.this.refreshStripExceptionConfig(agentConfig);
            }
        }
    }
}
