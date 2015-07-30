// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.agent.bridge.NoOpTransaction;
import com.newrelic.agent.bridge.NoOpCrossProcessState;
import com.newrelic.agent.bridge.CrossProcessState;
import java.util.Map;
import com.newrelic.agent.bridge.NoOpWebResponse;
import com.newrelic.agent.bridge.WebResponse;
import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import com.newrelic.api.agent.ApplicationNamePriority;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.agent.bridge.Transaction;

public class TransactionApiImpl implements Transaction
{
    public boolean equals(final Object obj) {
        if (!(obj instanceof TransactionApiImpl)) {
            return false;
        }
        final TransactionApiImpl objTxi = (TransactionApiImpl)obj;
        return this.getTransaction() == objTxi.getTransaction();
    }
    
    public int hashCode() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return (tx == null) ? 42 : tx.hashCode();
    }
    
    public com.newrelic.agent.Transaction getTransaction() {
        return com.newrelic.agent.Transaction.getTransaction(false);
    }
    
    public boolean registerAsyncActivity(final Object asyncContext) {
        return this.getTransaction().registerAsyncActivity(asyncContext);
    }
    
    public boolean startAsyncActivity(final Object asyncContext) {
        return this.getTransaction().startAsyncActivity(asyncContext);
    }
    
    public boolean ignoreAsyncActivity(final Object asyncContext) {
        return this.getTransaction().ignoreAsyncActivity(asyncContext);
    }
    
    public boolean setTransactionName(final TransactionNamePriority namePriority, final boolean override, final String category, final String... parts) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return tx != null && tx.setTransactionName(namePriority, override, category, parts);
    }
    
    public boolean isTransactionNameSet() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return tx != null && tx.isTransactionNameSet();
    }
    
    public TracedMethod getLastTracer() {
        return this.getTracedMethod();
    }
    
    public TracedMethod getTracedMethod() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx == null) {
            return null;
        }
        final TransactionActivity txa = tx.getTransactionActivity();
        if (txa == null) {
            return null;
        }
        return (TracedMethod)txa.getLastTracer();
    }
    
    public void ignore() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.ignore();
        }
    }
    
    public void ignoreApdex() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.ignoreApdex();
        }
    }
    
    public boolean setTransactionName(final com.newrelic.agent.bridge.TransactionNamePriority namePriority, final boolean override, final String category, final String... parts) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return tx != null && tx.setTransactionName(namePriority, override, category, parts);
    }
    
    public void beforeSendResponseHeaders() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.beforeSendResponseHeaders();
        }
    }
    
    public boolean isStarted() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return tx != null && tx.isStarted();
    }
    
    public void setApplicationName(final ApplicationNamePriority priority, final String appName) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.setApplicationName(priority, appName);
        }
    }
    
    public boolean isAutoAppNamingEnabled() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return tx != null && tx.isAutoAppNamingEnabled();
    }
    
    public boolean isWebRequestSet() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return tx != null && tx.isWebRequestSet();
    }
    
    public boolean isWebResponseSet() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return tx != null && tx.isWebResponseSet();
    }
    
    public void setWebRequest(final Request request) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.setWebRequest(request);
        }
    }
    
    public void setWebResponse(final Response response) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.setWebResponse(response);
        }
    }
    
    public WebResponse getWebResponse() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return (tx != null) ? tx.getWebResponse() : NoOpWebResponse.INSTANCE;
    }
    
    public void convertToWebTransaction() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.convertToWebTransaction();
        }
    }
    
    public boolean isWebTransaction() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return tx != null && tx.isWebTransaction();
    }
    
    public void requestInitialized(final Request request, final Response response) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.requestInitialized(request, response);
        }
    }
    
    public void requestDestroyed() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.requestDestroyed();
        }
    }
    
    public void saveMessageParameters(final Map<String, String> parameters) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.saveMessageParameters(parameters);
        }
    }
    
    public CrossProcessState getCrossProcessState() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return (tx != null) ? tx.getCrossProcessState() : NoOpCrossProcessState.INSTANCE;
    }
    
    public com.newrelic.agent.bridge.TracedMethod startFlyweightTracer() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx == null || !tx.isStarted()) {
            return null;
        }
        return tx.getTransactionActivity().startFlyweightTracer();
    }
    
    public void finishFlyweightTracer(final com.newrelic.agent.bridge.TracedMethod parent, final long startInNanos, final long finishInNanos, final String className, final String methodName, final String methodDesc, final String metricName, final String[] rollupMetricNames) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null && tx.isStarted()) {
            tx.getTransactionActivity().finishFlyweightTracer(parent, startInNanos, finishInNanos, className, methodName, methodDesc, metricName, rollupMetricNames);
        }
    }
    
    public Map<String, Object> getAgentAttributes() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return (tx != null) ? tx.getAgentAttributes() : NoOpTransaction.INSTANCE.getAgentAttributes();
    }
    
    public void provideHeaders(final InboundHeaders headers) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.provideHeaders(headers);
        }
    }
    
    public String getRequestMetadata() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return (tx != null) ? tx.getCrossProcessState().getRequestMetadata() : NoOpCrossProcessState.INSTANCE.getRequestMetadata();
    }
    
    public void processRequestMetadata(final String metadata) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.getCrossProcessState().processRequestMetadata(metadata);
        }
    }
    
    public String getResponseMetadata() {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        return (tx != null) ? tx.getCrossProcessState().getResponseMetadata() : NoOpCrossProcessState.INSTANCE.getResponseMetadata();
    }
    
    public void processResponseMetadata(final String metadata) {
        final com.newrelic.agent.Transaction tx = this.getTransaction();
        if (tx != null) {
            tx.getCrossProcessState().processResponseMetadata(metadata);
        }
    }
}
