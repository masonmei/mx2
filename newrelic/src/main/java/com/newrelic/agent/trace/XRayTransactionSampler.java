// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.trace;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import com.newrelic.agent.TransactionData;
import java.util.List;
import com.newrelic.agent.xray.XRaySession;

public class XRayTransactionSampler implements ITransactionSampler
{
    static final int TRACES_TO_KEEP = 10;
    private final XRaySession session;
    private final String applicationName;
    private final List<TransactionData> data;
    
    public XRayTransactionSampler(final XRaySession session) {
        this.data = new CopyOnWriteArrayList<TransactionData>();
        this.session = session;
        this.applicationName = session.getApplicationName();
    }
    
    public boolean noticeTransaction(final TransactionData td) {
        if (this.session.sessionHasExpired()) {
            return false;
        }
        if (this.data.size() >= 10) {
            return false;
        }
        final String appName = td.getApplicationName();
        final String transactionName = td.getBlameOrRootMetricName();
        if (this.isTransactionOfInterest(appName, transactionName)) {
            this.data.add(td);
            this.session.incrementCount();
            return true;
        }
        return false;
    }
    
    boolean isTransactionOfInterest(final String appName, final String transactionName) {
        return this.applicationName.equals(appName) && transactionName.equals(this.session.getKeyTransactionName());
    }
    
    public List<TransactionTrace> harvest(final String appName) {
        final List<TransactionTrace> tracesToReturn = new ArrayList<TransactionTrace>();
        for (final TransactionData td : this.data) {
            final TransactionTrace trace = TransactionTrace.getTransactionTrace(td);
            trace.setXraySessionId(this.session.getxRayId());
            tracesToReturn.add(trace);
        }
        this.data.clear();
        return tracesToReturn;
    }
    
    public void stop() {
    }
    
    public long getMaxDurationInNanos() {
        return 0L;
    }
}
