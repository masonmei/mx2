// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.trace;

import com.newrelic.agent.xray.XRaySession;
import com.newrelic.agent.stats.TransactionStats;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.IgnoreSilentlyException;
import java.util.Collections;
import java.util.Arrays;
import com.newrelic.agent.IRPMService;
import java.util.Collection;
import java.util.ArrayList;
import com.newrelic.agent.stats.StatsEngine;
import java.util.Iterator;
import com.newrelic.agent.dispatchers.Dispatcher;
import com.newrelic.agent.TransactionData;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.management.ManagementFactory;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import com.newrelic.agent.config.ConfigService;
import java.util.concurrent.ConcurrentMap;
import java.lang.management.ThreadMXBean;
import com.newrelic.agent.xray.XRaySessionListener;
import com.newrelic.agent.TransactionListener;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.AbstractService;

public class TransactionTraceService extends AbstractService implements HarvestListener, TransactionListener, XRaySessionListener
{
    private static final int INITIAL_TRACE_LIMIT = 5;
    private final ThreadMXBean threadMXBean;
    private final ConcurrentMap<String, ITransactionSampler> transactionTraceBuckets;
    private final boolean autoAppNameEnabled;
    private final boolean threadCpuTimeEnabled;
    private final ConfigService configService;
    private final ITransactionSampler requestTraceBucket;
    private final ITransactionSampler backgroundTraceBucket;
    private final SyntheticsTransactionSampler syntheticsTransactionSampler;
    private final List<ITransactionSampler> transactionSamplers;
    private final ConcurrentMap<Long, XRayTransactionSampler> xraySamplers;
    private final List<XRayTransactionSampler> xraySamplersPendingRemoval;
    
    public TransactionTraceService() {
        super(TransactionTraceService.class.getSimpleName());
        this.transactionSamplers = new CopyOnWriteArrayList<ITransactionSampler>();
        this.xraySamplersPendingRemoval = new CopyOnWriteArrayList<XRayTransactionSampler>();
        this.requestTraceBucket = this.createBucket();
        this.backgroundTraceBucket = this.createBucket();
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.transactionTraceBuckets = new ConcurrentHashMap<String, ITransactionSampler>();
        this.configService = ServiceFactory.getConfigService();
        final AgentConfig config = this.configService.getDefaultAgentConfig();
        this.autoAppNameEnabled = config.isAutoAppNamingEnabled();
        this.threadCpuTimeEnabled = this.initThreadCPUEnabled(config);
        this.syntheticsTransactionSampler = new SyntheticsTransactionSampler();
        this.xraySamplers = new ConcurrentHashMap<Long, XRayTransactionSampler>();
    }
    
    public ThreadMXBean getThreadMXBean() {
        return this.threadMXBean;
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public void addTransactionTraceSampler(final ITransactionSampler transactionSampler) {
        this.transactionSamplers.add(transactionSampler);
    }
    
    public void removeTransactionTraceSampler(final ITransactionSampler transactionSampler) {
        this.transactionSamplers.remove(transactionSampler);
    }
    
    public boolean isThreadCpuTimeEnabled() {
        return this.threadCpuTimeEnabled;
    }
    
    private boolean initThreadCPUEnabled(final AgentConfig config) {
        boolean result = true;
        final Boolean prop = config.getProperty("thread_cpu_time_enabled");
        if (prop == null) {
            final String vendor = System.getProperty("java.vendor");
            return "IBM Corporation".equals(vendor) && false;
        }
        result = prop;
        if (!result) {
            return false;
        }
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        return threadMXBean.isThreadCpuTimeSupported() && threadMXBean.isThreadCpuTimeEnabled();
    }
    
    private ITransactionSampler getTransactionTraceBucket(final TransactionData transactionData) {
        if (this.autoAppNameEnabled) {
            return this.getOrCreateTransactionTraceBucket(transactionData.getApplicationName());
        }
        return this.getTransactionBucket(transactionData.getDispatcher());
    }
    
    private ITransactionSampler getOrCreateTransactionTraceBucket(final String appName) {
        ITransactionSampler bucket = this.getTransactionTraceBucket(appName);
        if (bucket == null) {
            bucket = this.createBucket();
            final ITransactionSampler oldBucket = this.transactionTraceBuckets.putIfAbsent(appName, bucket);
            if (oldBucket != null) {
                return oldBucket;
            }
        }
        return bucket;
    }
    
    private ITransactionSampler getTransactionTraceBucket(final String bucketName) {
        if ("request" == bucketName) {
            return this.requestTraceBucket;
        }
        if ("background" == bucketName) {
            return this.backgroundTraceBucket;
        }
        return this.transactionTraceBuckets.get(bucketName);
    }
    
    private String getTransactionBucketName(final Dispatcher dispatcher) {
        return dispatcher.isWebTransaction() ? "request" : "background";
    }
    
    private void noticeTransaction(final TransactionData transactionData) {
        if (this.syntheticsTransactionSampler.noticeTransaction(transactionData)) {
            return;
        }
        for (final ITransactionSampler transactionSampler : this.transactionSamplers) {
            if (transactionSampler.noticeTransaction(transactionData)) {
                return;
            }
        }
        final ITransactionSampler bucket = this.getTransactionTraceBucket(transactionData);
        if (bucket != null) {
            bucket.noticeTransaction(transactionData);
        }
    }
    
    private ITransactionSampler getTransactionBucket(final Dispatcher dispatcher) {
        ITransactionSampler bucket = this.getTransactionTraceBucket(this.getTransactionBucketName(dispatcher));
        if (bucket == null) {
            bucket = this.requestTraceBucket;
        }
        return bucket;
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
    }
    
    public void afterHarvest(final String appName) {
        final List<TransactionTrace> traces = new ArrayList<TransactionTrace>();
        if (this.autoAppNameEnabled) {
            traces.addAll(this.getExpensiveTransaction(appName));
        }
        else {
            traces.addAll(this.getAllExpensiveTransactions(appName));
        }
        traces.addAll(this.syntheticsTransactionSampler.harvest(appName));
        for (final ITransactionSampler transactionSampler : this.transactionSamplers) {
            traces.addAll(transactionSampler.harvest(appName));
        }
        if (!traces.isEmpty()) {
            final IRPMService rpmService = ServiceFactory.getRPMService(appName);
            this.sendTraces(rpmService, traces);
        }
        if (!this.xraySamplersPendingRemoval.isEmpty()) {
            for (final XRayTransactionSampler samplerToRemove : this.xraySamplersPendingRemoval) {
                this.removeTransactionTraceSampler(samplerToRemove);
            }
            this.xraySamplersPendingRemoval.clear();
        }
    }
    
    private List<TransactionTrace> getAllExpensiveTransactions(final String appName) {
        final List<TransactionTrace> traces = new ArrayList<TransactionTrace>();
        final List<ITransactionSampler> allBuckets = new ArrayList<ITransactionSampler>(Arrays.asList(this.requestTraceBucket, this.backgroundTraceBucket));
        allBuckets.addAll(this.transactionTraceBuckets.values());
        for (final ITransactionSampler bucket : allBuckets) {
            final List<TransactionTrace> expensiveTransactions = bucket.harvest(appName);
            if (expensiveTransactions != null) {
                traces.addAll(expensiveTransactions);
            }
        }
        return traces;
    }
    
    public List<TransactionTrace> getExpensiveTransaction(final String appName) {
        final ITransactionSampler bucket = this.getTransactionTraceBucket(appName);
        if (bucket != null) {
            return bucket.harvest(appName);
        }
        return Collections.emptyList();
    }
    
    private void sendTraces(final IRPMService rpmService, final List<TransactionTrace> traces) {
        if (!rpmService.isConnected()) {
            return;
        }
        try {
            rpmService.sendTransactionTraceData(traces);
        }
        catch (IgnoreSilentlyException e2) {}
        catch (Exception e) {
            if (this.getLogger().isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Error sending transaction trace data to {0} for {1}: {2}", rpmService.getHostString(), rpmService.getApplicationName(), e.getMessage());
                if (this.getLogger().isLoggable(Level.FINEST)) {
                    this.getLogger().log(Level.FINEST, msg, e);
                }
                else {
                    this.getLogger().finer(msg);
                }
            }
        }
    }
    
    protected void doStart() {
        ServiceFactory.getTransactionService().addTransactionListener(this);
        ServiceFactory.getHarvestService().addHarvestListener(this);
        ServiceFactory.getXRaySessionService().addListener(this);
        RandomTransactionSampler.startSampler(5);
    }
    
    protected void doStop() {
        for (final ITransactionSampler bucket : this.transactionTraceBuckets.values()) {
            bucket.stop();
        }
        this.transactionTraceBuckets.clear();
    }
    
    private ITransactionSampler createBucket() {
        return new TransactionTraceBucket();
    }
    
    public void dispatcherTransactionFinished(final TransactionData transactionData, final TransactionStats transactionStats) {
        if (!transactionData.getTransactionTracerConfig().isEnabled()) {
            return;
        }
        this.noticeTransaction(transactionData);
    }
    
    public boolean isInteresting(final Dispatcher dispatcher, final long responseTimeNs) {
        return this.autoAppNameEnabled || responseTimeNs > this.getTransactionBucket(dispatcher).getMaxDurationInNanos();
    }
    
    public void xraySessionCreated(final XRaySession session) {
        this.getLogger().finer("TT service notified of X-Ray Session creation: " + session);
        final XRayTransactionSampler sampler = new XRayTransactionSampler(session);
        this.xraySamplers.put(session.getxRayId(), sampler);
        this.transactionSamplers.add(0, sampler);
    }
    
    public void xraySessionRemoved(final XRaySession session) {
        this.getLogger().finer("TT service notified of X-Ray Session removal: " + session);
        if (null != session) {
            final XRayTransactionSampler samplerToRemove = this.xraySamplers.remove(session.getxRayId());
            if (samplerToRemove != null) {
                this.xraySamplersPendingRemoval.add(samplerToRemove);
            }
        }
    }
}
