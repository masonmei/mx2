// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.dispatchers;

import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.IRPMService;
import com.newrelic.agent.stats.ApdexStats;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.service.ServiceFactory;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.transaction.TransactionNamer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.transaction.WebTransactionNamer;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.servlet.ServletUtils;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.servlet.ExternalTimeTracker;
import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import com.newrelic.agent.bridge.StatusCodePolicy;
import com.newrelic.agent.bridge.WebResponse;

public class WebRequestDispatcher extends DefaultDispatcher implements WebResponse
{
    private static final String UNKNOWN_URI = "/Unknown";
    private static final StatusCodePolicy LAST_STATUS_CODE_POLICY;
    private static final StatusCodePolicy ERROR_STATUS_CODE_POLICY;
    private static final StatusCodePolicy FREEZE_STATUS_CODE_POLICY;
    private Request request;
    private Response response;
    private String requestURI;
    private ExternalTimeTracker externalTimeTracker;
    private int statusCode;
    private String statusMessage;
    private StatusCodePolicy statusCodePolicy;
    
    public WebRequestDispatcher(final Request request, final Response response, final Transaction transaction) {
        super(transaction);
        final boolean isLastStatusCodePolicy = (Boolean)transaction.getAgentConfig().getValue("last_status_code_policy", (Object)Boolean.TRUE);
        this.statusCodePolicy = (isLastStatusCodePolicy ? WebRequestDispatcher.LAST_STATUS_CODE_POLICY : WebRequestDispatcher.ERROR_STATUS_CODE_POLICY);
        this.request = request;
        this.response = response;
        this.externalTimeTracker = ExternalTimeTracker.create(request, transaction.getWallClockStartTimeMs());
    }
    
    public Request getRequest() {
        return this.request;
    }
    
    public void setRequest(final Request request) {
        this.externalTimeTracker = ExternalTimeTracker.create(request, this.getTransaction().getWallClockStartTimeMs());
        this.request = request;
    }
    
    public Response getResponse() {
        return this.response;
    }
    
    public void setResponse(final Response response) {
        this.response = response;
    }
    
    public void transactionFinished(final String transactionName, final TransactionStats stats) {
        if (this.request != null) {
            try {
                this.setStatus();
                this.freezeStatus();
                this.setStatusMessage();
                this.doRecordMetrics(transactionName, stats);
                ServletUtils.recordParameters(this.getTransaction(), this.request);
                this.storeReferrer();
                if (this.getStatus() > 0) {
                    this.getTransaction().getAgentAttributes().put("httpResponseCode", String.valueOf(this.getStatus()));
                }
                if (this.getStatusMessage() != null) {
                    this.getTransaction().getAgentAttributes().put("httpResponseMessage", this.getStatusMessage());
                }
                this.request = null;
                this.response = null;
            }
            finally {
                this.request = null;
                this.response = null;
            }
        }
    }
    
    public final String getUri() {
        if (this.requestURI == null) {
            this.requestURI = this.initializeRequestURI();
        }
        return this.requestURI;
    }
    
    public void setTransactionName() {
        if (Transaction.isDummyRequest(this.request)) {
            final Tracer rootTracer = this.getTransaction().getRootTracer();
            if (rootTracer != null) {
                rootTracer.nameTransaction(TransactionNamePriority.REQUEST_URI);
            }
        }
        final TransactionNamer tn = WebTransactionNamer.create(this.getTransaction(), this.getUri());
        tn.setTransactionName();
    }
    
    private String initializeRequestURI() {
        String result = "/Unknown";
        if (this.request == null) {
            return result;
        }
        try {
            final String uri = this.request.getRequestURI();
            if (uri == null || uri.length() == 0) {
                Agent.LOG.log(Level.FINER, "requestURI is null: setting requestURI to {0}", new Object[] { result });
            }
            else {
                result = ServiceFactory.getNormalizationService().getUrlBeforeParameters(uri);
            }
        }
        catch (Throwable e) {
            Agent.LOG.severe("Error calling requestURI: " + e.toString());
            Agent.LOG.log(Level.FINEST, e.toString(), e);
            result = "/Unknown";
        }
        return result;
    }
    
    private void storeReferrer() {
        try {
            String referer = this.request.getHeader("Referer");
            if (referer != null) {
                referer = referer.split("\\?")[0];
                this.getTransaction().getAgentAttributes().put("request.headers.referer", referer);
            }
        }
        catch (Throwable e) {
            Agent.LOG.finer("Error getting referer: " + e.toString());
            Agent.LOG.log(Level.FINEST, e.toString(), e);
        }
    }
    
    public void freezeStatus() {
        this.statusCodePolicy = WebRequestDispatcher.FREEZE_STATUS_CODE_POLICY;
        Agent.LOG.log(Level.FINER, "Freezing status code to {0}", new Object[] { this.getStatus() });
    }
    
    private void setStatus() {
        if (this.response != null) {
            try {
                this.setStatus(this.response.getStatus());
            }
            catch (Exception e) {
                Agent.LOG.log(Level.FINER, "Failed to get response status code {0}", new Object[] { e.toString() });
            }
        }
    }
    
    private void setStatusMessage() {
        if (this.response != null && this.getStatusMessage() == null && this.getStatus() >= 400) {
            try {
                this.setStatusMessage(this.response.getStatusMessage());
            }
            catch (Exception e) {
                Agent.LOG.log(Level.FINER, "Failed to get response status message {0}", new Object[] { e.toString() });
            }
        }
    }
    
    private void doRecordMetrics(final String transactionName, final TransactionStats stats) {
        this.recordHeaderMetrics(stats);
        this.recordApdexMetrics(transactionName, stats);
        this.recordDispatcherMetrics(transactionName, stats);
    }
    
    public void recordHeaderMetrics(final TransactionStats statsEngine) {
        this.externalTimeTracker.recordMetrics(statsEngine);
    }
    
    public long getQueueTime() {
        return this.externalTimeTracker.getExternalTime();
    }
    
    private void recordDispatcherMetrics(final String frontendMetricName, final TransactionStats stats) {
        if (frontendMetricName == null || frontendMetricName.length() == 0) {
            return;
        }
        final long frontendTimeInNanos = this.getTransaction().getTransactionTimer().getResponseTime();
        stats.getUnscopedStats().getResponseTimeStats(frontendMetricName).recordResponseTimeInNanos(frontendTimeInNanos, 0L);
        stats.getUnscopedStats().getResponseTimeStats("WebTransaction").recordResponseTimeInNanos(frontendTimeInNanos);
        stats.getUnscopedStats().getResponseTimeStats("HttpDispatcher").recordResponseTimeInNanos(frontendTimeInNanos);
        if (this.getStatus() > 0) {
            final String metricName = Strings.join("Network/Inbound/StatusCode/", String.valueOf(this.getStatus()));
            stats.getUnscopedStats().getResponseTimeStats(metricName).recordResponseTimeInNanos(frontendTimeInNanos);
        }
        final String totalTimeMetric = this.getTransTotalName(frontendMetricName, "WebTransaction");
        if (totalTimeMetric != null && totalTimeMetric.length() > 0) {
            stats.getUnscopedStats().getResponseTimeStats(totalTimeMetric).recordResponseTimeInNanos(this.getTransaction().getTransactionTimer().getTotalTime());
        }
        stats.getUnscopedStats().getResponseTimeStats("WebTransactionTotalTime").recordResponseTimeInNanos(this.getTransaction().getTransactionTimer().getTotalTime());
    }
    
    private void recordApdexMetrics(final String frontendMetricName, final TransactionStats stats) {
        if (frontendMetricName == null || frontendMetricName.length() == 0) {
            return;
        }
        if (!this.getTransaction().getAgentConfig().isApdexTSet()) {
            return;
        }
        if (this.isIgnoreApdex()) {
            Agent.LOG.log(Level.FINE, "Ignoring transaction for apdex {0}", new Object[] { frontendMetricName });
            return;
        }
        final String frontendApdexMetricName = this.getApdexMetricName(frontendMetricName, "WebTransaction", "Apdex");
        if (frontendApdexMetricName == null || frontendApdexMetricName.length() == 0) {
            return;
        }
        final long apdexT = this.getTransaction().getAgentConfig().getApdexTInMillis(frontendMetricName);
        final ApdexStats apdexStats = stats.getUnscopedStats().getApdexStats(frontendApdexMetricName);
        final ApdexStats overallApdexStats = stats.getUnscopedStats().getApdexStats("Apdex");
        if (this.isApdexFrustrating()) {
            apdexStats.recordApdexFrustrated();
            overallApdexStats.recordApdexFrustrated();
        }
        else {
            final long responseTimeInMillis = this.getTransaction().getTransactionTimer().getResponseTimeInMilliseconds() + this.externalTimeTracker.getExternalTime();
            apdexStats.recordApdexResponseTime(responseTimeInMillis, apdexT);
            overallApdexStats.recordApdexResponseTime(responseTimeInMillis, apdexT);
        }
    }
    
    public boolean isApdexFrustrating() {
        final String appName = this.getTransaction().getPriorityApplicationName().getName();
        final IRPMService rpmService = ServiceFactory.getRPMService(appName);
        return this.getTransaction().getStatus() >= 400 && !rpmService.getErrorService().isIgnoredError(this.getTransaction().getStatus(), this.getTransaction().getReportError());
    }
    
    public TransactionTracerConfig getTransactionTracerConfig() {
        return this.getTransaction().getAgentConfig().getRequestTransactionTracerConfig();
    }
    
    public boolean isWebTransaction() {
        return true;
    }
    
    public boolean isAsyncTransaction() {
        return false;
    }
    
    public String getCookieValue(final String name) {
        if (this.request == null) {
            return null;
        }
        return this.request.getCookieValue(name);
    }
    
    public String getHeader(final String name) {
        if (this.request == null) {
            return null;
        }
        return this.request.getHeader(name);
    }
    
    public void setStatus(final int statusCode) {
        Agent.LOG.log(Level.FINEST, "Called setStatus: {0}", new Object[] { statusCode });
        if (statusCode <= 0 || statusCode == this.statusCode) {
            return;
        }
        final int nextStatusCode = this.statusCodePolicy.nextStatus(this.statusCode, statusCode);
        if (nextStatusCode != this.statusCode) {
            Agent.LOG.log(Level.FINER, "Setting status to {0}", new Object[] { nextStatusCode });
        }
        this.statusCode = nextStatusCode;
    }
    
    public int getStatus() {
        return this.statusCode;
    }
    
    public void setStatusMessage(final String message) {
        this.statusMessage = message;
    }
    
    public String getStatusMessage() {
        return this.statusMessage;
    }
    
    static {
        LAST_STATUS_CODE_POLICY = (StatusCodePolicy)new StatusCodePolicy() {
            public int nextStatus(final int currentStatus, final int lastStatus) {
                return lastStatus;
            }
        };
        ERROR_STATUS_CODE_POLICY = (StatusCodePolicy)new StatusCodePolicy() {
            public int nextStatus(final int currentStatus, final int lastStatus) {
                return (currentStatus < 400) ? lastStatus : currentStatus;
            }
        };
        FREEZE_STATUS_CODE_POLICY = (StatusCodePolicy)new StatusCodePolicy() {
            public int nextStatus(final int currentStatus, final int lastStatus) {
                return currentStatus;
            }
        };
    }
}
