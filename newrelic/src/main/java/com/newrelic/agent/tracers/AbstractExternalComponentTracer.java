// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.util.Strings;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.stats.TransactionStats;
import java.net.UnknownHostException;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.Transaction;

public abstract class AbstractExternalComponentTracer extends DefaultTracer implements IgnoreChildSocketCalls
{
    private static final String UNKNOWN_HOST = "UnknownHost";
    private String host;
    
    public AbstractExternalComponentTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final String library, final String uri, final String... operations) {
        this(transaction, sig, object, host, library, false, uri, operations);
    }
    
    public AbstractExternalComponentTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final String library, final boolean includeOperationInMetric, final String uri, final String... operations) {
        super(transaction, sig, object, ExternalComponentNameFormat.create(host, library, includeOperationInMetric, uri, operations));
        this.host = host;
    }
    
    public AbstractExternalComponentTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final String host, final MetricNameFormat metricNameFormat) {
        super(transaction, sig, object, metricNameFormat);
        this.host = host;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void finish(final Throwable throwable) {
        if (throwable instanceof UnknownHostException) {
            this.host = "UnknownHost";
            final MetricNameFormat metricNameFormat = this.getMetricNameFormat();
            if (metricNameFormat instanceof ExternalComponentNameFormat) {
                this.setMetricNameFormat(((ExternalComponentNameFormat)metricNameFormat).cloneWithNewHost("UnknownHost"));
            }
        }
        super.finish(throwable);
    }
    
    protected void doRecordMetrics(final TransactionStats transactionStats) {
        super.doRecordMetrics(transactionStats);
        transactionStats.getUnscopedStats().getResponseTimeStats("External/all").recordResponseTime(this.getExclusiveDuration(), TimeUnit.NANOSECONDS);
        transactionStats.getUnscopedStats().getResponseTimeStats(this.getTransaction().isWebTransaction() ? "External/allWeb" : "External/allOther").recordResponseTime(this.getExclusiveDuration(), TimeUnit.NANOSECONDS);
        final String hostRollupMetricName = Strings.join('/', "External", this.getHost(), "all");
        transactionStats.getUnscopedStats().getResponseTimeStats(hostRollupMetricName).recordResponseTime(this.getExclusiveDuration(), TimeUnit.NANOSECONDS);
    }
}
