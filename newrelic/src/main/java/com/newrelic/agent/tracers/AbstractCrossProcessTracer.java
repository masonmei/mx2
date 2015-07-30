// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import java.util.concurrent.TimeUnit;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.Transaction;
import com.newrelic.api.agent.InboundHeaders;

public abstract class AbstractCrossProcessTracer extends AbstractExternalComponentTracer implements InboundHeaders
{
    private CrossProcessNameFormat crossProcessFormat;
    private final String uri;
    private Object response;
    
    public AbstractCrossProcessTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final String library, final String uri, final String methodName) {
        super(transaction, sig, object, host, library, uri, new String[] { methodName });
        this.uri = uri;
    }
    
    protected void doFinish(final int opcode, final Object returnValue) {
        this.response = returnValue;
        this.getTransaction().getCrossProcessState().processInboundResponseHeaders((InboundHeaders)this, (TracedMethod)this, this.getHost(), this.uri, false);
        super.doFinish(opcode, returnValue);
    }
    
    public void doFinish(final Throwable throwable) {
        this.getTransaction().getCrossProcessState().processInboundResponseHeaders((InboundHeaders)this, (TracedMethod)this, this.getHost(), this.uri, false);
        super.doFinish(throwable);
    }
    
    public HeaderType getHeaderType() {
        return HeaderType.HTTP;
    }
    
    public String getHeader(final String name) {
        return this.getHeaderValue(this.response, name);
    }
    
    protected abstract String getHeaderValue(final Object p0, final String p1);
    
    public void setMetricNameFormat(final MetricNameFormat nameFormat) {
        super.setMetricNameFormat(nameFormat);
        if (nameFormat instanceof CrossProcessNameFormat) {
            this.crossProcessFormat = (CrossProcessNameFormat)nameFormat;
        }
    }
    
    protected void doRecordMetrics(final TransactionStats transactionStats) {
        super.doRecordMetrics(transactionStats);
        this.doRecordCrossProcessRollup(transactionStats);
    }
    
    protected void doRecordCrossProcessRollup(final TransactionStats transactionStats) {
        if (this.crossProcessFormat != null) {
            final String hostCrossProcessIdRollupMetricName = this.crossProcessFormat.getHostCrossProcessIdRollupMetricName();
            transactionStats.getUnscopedStats().getResponseTimeStats(hostCrossProcessIdRollupMetricName).recordResponseTime(this.getExclusiveDuration(), TimeUnit.NANOSECONDS);
        }
    }
    
    public String getUri() {
        return this.uri;
    }
}
