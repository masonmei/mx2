// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import com.newrelic.agent.service.ServiceFactory;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.Transaction;

public class WebTransactionNamer extends AbstractTransactionNamer
{
    private WebTransactionNamer(final Transaction tx, final String requestUri) {
        super(tx, requestUri);
    }
    
    public void setTransactionName() {
        if (!this.canSetTransactionName(TransactionNamePriority.STATUS_CODE)) {
            return;
        }
        final Transaction tx = this.getTransaction();
        final int responseStatusCode = tx.getStatus();
        if (responseStatusCode >= 400) {
            final String normalizedStatus = normalizeStatus(responseStatusCode);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using response status", normalizedStatus);
                Agent.LOG.finer(msg);
            }
            if (this.canSetTransactionName(TransactionNamePriority.STATUS_CODE)) {
                this.setTransactionName(normalizedStatus, "NormalizedUri", TransactionNamePriority.STATUS_CODE);
                tx.freezeStatus();
            }
            return;
        }
        if (!this.canSetTransactionName()) {
            return;
        }
        final String requestUri = this.getUri();
        if (requestUri == null) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Setting transaction name to \"{0}\" because request uri is null", "Unknown");
                Agent.LOG.finer(msg);
            }
            this.setTransactionName("Unknown", "NormalizedUri", TransactionNamePriority.REQUEST_URI);
            return;
        }
        final String appName = tx.getPriorityApplicationName().getName();
        final String normalizedUri = ServiceFactory.getNormalizationService().getUrlNormalizer(appName).normalize(requestUri);
        if (normalizedUri == null) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg2 = "Ignoring transaction because normalized request uri is null";
                Agent.LOG.finer(msg2);
            }
            tx.setIgnore(true);
            return;
        }
        if (normalizedUri == requestUri) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg2 = MessageFormat.format("Setting transaction name to \"{0}\" using request uri", requestUri);
                Agent.LOG.finer(msg2);
            }
            this.setTransactionName(requestUri, "Uri", TransactionNamePriority.REQUEST_URI);
            return;
        }
        if (Agent.LOG.isLoggable(Level.FINER)) {
            final String msg2 = MessageFormat.format("Setting transaction name to \"{0}\" using normalized request uri", normalizedUri);
            Agent.LOG.finer(msg2);
        }
        this.setTransactionName(normalizedUri, "NormalizedUri", TransactionNamePriority.REQUEST_URI);
    }
    
    private static String normalizeStatus(final int responseStatus) {
        return "/" + String.valueOf(responseStatus) + "/*";
    }
    
    public static TransactionNamer create(final Transaction tx, final String requestUri) {
        return new WebTransactionNamer(tx, requestUri);
    }
}
