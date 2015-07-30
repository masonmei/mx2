// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.api.agent.HeaderType;
import java.util.Enumeration;
import com.newrelic.agent.tracers.ClassMethodSignatures;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.newrelic.agent.messaging.MessagingUtil;
import com.newrelic.agent.application.ApplicationNamingPolicy;
import com.newrelic.agent.application.HigherPriorityApplicationNamingPolicy;
import com.newrelic.agent.application.SameOrHigherPriorityApplicationNamingPolicy;
import com.newrelic.api.agent.ApplicationNamePriority;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.agent.bridge.ExitTracer;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.CrossProcessState;
import com.newrelic.agent.browser.BrowserTransactionStateImpl;
import com.newrelic.agent.database.CachingDatabaseStatementParser;
import com.newrelic.agent.servlet.ServletUtils;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.service.ServiceUtils;
import com.newrelic.agent.tracers.TransactionActivityInitiator;
import java.util.List;
import com.newrelic.agent.dispatchers.WebRequestDispatcher;
import com.newrelic.agent.transaction.TransactionCache;
import com.newrelic.agent.normalization.Normalizer;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import java.text.MessageFormat;
import com.newrelic.agent.util.Strings;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.agent.config.CrossProcessConfig;
import com.newrelic.agent.config.TransactionTracerConfig;
import java.util.Iterator;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import com.newrelic.agent.trace.TransactionTraceService;
import com.newrelic.agent.deps.com.google.common.collect.MapMaker;
import com.newrelic.agent.trace.TransactionGuidFactory;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.stats.AbstractMetricAggregator;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.application.PriorityApplicationName;
import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import com.newrelic.agent.bridge.WebResponse;
import com.newrelic.api.agent.MetricAggregator;
import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.agent.browser.BrowserTransactionState;
import com.newrelic.agent.sql.SqlTracerListener;
import com.newrelic.agent.database.DatabaseStatementParser;
import com.newrelic.agent.transaction.PriorityTransactionName;
import com.newrelic.agent.transaction.ConnectionCache;
import com.newrelic.agent.transaction.TransactionTimer;
import com.newrelic.agent.dispatchers.Dispatcher;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.api.agent.Insights;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import com.newrelic.agent.transaction.TransactionCounts;
import com.newrelic.agent.tracers.ClassMethodSignature;

public class Transaction implements ITransaction
{
    static final ClassMethodSignature REQUEST_INITIALIZED_CLASS_SIGNATURE;
    static final int REQUEST_INITIALIZED_CLASS_SIGNATURE_ID;
    private static final String THREAD_ASSERTION_FAILURE = "Thread assertion failed!";
    private static final ThreadLocal<Transaction> transactionHolder;
    private final IAgent agent;
    private final String guid;
    private final boolean ttEnabled;
    private final TransactionCounts counts;
    private final boolean autoAppNamingEnabled;
    private final boolean transactionNamingEnabled;
    private final boolean ignoreErrorPriority;
    private final AtomicReference<TransactionErrorPriority> throwablePriority;
    private final Object lock;
    private final Map<Object, TransactionActivity> runningChildren;
    private final Map<Object, TransactionActivity> finishedChildren;
    private final AtomicInteger nextActivityId;
    private final AtomicReference<AppNameAndConfig> appNameAndConfig;
    private final Map<String, Object> internalParameters;
    private final Map<String, Map<String, String>> prefixedAgentAttributes;
    private final Map<String, Object> agentAttributes;
    private final Map<String, Object> intrinsicAttributes;
    private final Map<String, Object> userAttributes;
    private final Map<String, String> errorAttributes;
    private final Insights insights;
    private final Map<Object, Tracer> contextToTracer;
    private final Map<Object, Tracer> timedOutKeys;
    private volatile long wallClockStartTimeMs;
    private volatile long startGCTimeInMillis;
    private volatile Throwable throwable;
    private volatile boolean ignore;
    private volatile Dispatcher dispatcher;
    private volatile Tracer rootTracer;
    private volatile TransactionTimer transactionTime;
    private volatile TransactionState transactionState;
    private volatile TransactionActivity initialActivity;
    private volatile ConnectionCache connectionCache;
    private volatile PriorityTransactionName priorityTransactionName;
    private CrossProcessTransactionState crossProcessTransactionState;
    private DatabaseStatementParser databaseStatementParser;
    private String normalizedUri;
    private SqlTracerListener sqlTracerListener;
    private BrowserTransactionState browserTransactionState;
    private InboundHeaders providedHeaders;
    private InboundHeaderState inboundHeaderState;
    private final LegacyState legacyState;
    private final MetricAggregator metricAggregator;
    private static DummyTransaction dummyTransaction;
    protected static final WebResponse DEFAULT_RESPONSE;
    private static final Request DUMMY_REQUEST;
    private static final Response DUMMY_RESPONSE;
    private static final int REQUEST_TRACER_FLAGS = 14;
    private final Object requestStateChangeLock;
    
    protected Transaction() {
        this.throwablePriority = new AtomicReference<TransactionErrorPriority>();
        this.lock = new Object();
        this.nextActivityId = new AtomicInteger(0);
        this.appNameAndConfig = new AtomicReference<AppNameAndConfig>(new AppNameAndConfig(PriorityApplicationName.NONE, null));
        this.transactionState = new TransactionStateImpl();
        this.initialActivity = null;
        this.connectionCache = null;
        this.priorityTransactionName = PriorityTransactionName.NONE;
        this.legacyState = new LegacyState();
        this.metricAggregator = (MetricAggregator)new AbstractMetricAggregator() {
            protected void doRecordResponseTimeMetric(final String name, final long totalTime, final long exclusiveTime, final TimeUnit timeUnit) {
                Transaction.this.getTransactionActivity().getTransactionStats().getUnscopedStats().getResponseTimeStats(name).recordResponseTime(totalTime, exclusiveTime, timeUnit);
            }
            
            protected void doRecordMetric(final String name, final float value) {
                Transaction.this.getTransactionActivity().getTransactionStats().getUnscopedStats().getStats(name).recordDataPoint(value);
            }
            
            protected void doIncrementCounter(final String name, final int count) {
                Transaction.this.getTransactionActivity().getTransactionStats().getUnscopedStats().getStats(name).incrementCallCount(count);
            }
        };
        this.requestStateChangeLock = new Object();
        Agent.LOG.log(Level.FINE, "create Transaction {0}", new Object[] { this });
        if (Agent.LOG.isFinestEnabled() && Agent.isDebugEnabled()) {
            Agent.LOG.log(Level.FINEST, "backtrace: {0}", new Object[] { Arrays.toString(Thread.currentThread().getStackTrace()) });
        }
        final AgentConfig defaultConfig = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.agent = ServiceFactory.getAgent();
        this.guid = TransactionGuidFactory.generateGuid();
        this.autoAppNamingEnabled = defaultConfig.isAutoAppNamingEnabled();
        this.transactionNamingEnabled = this.initializeTransactionNamingEnabled(defaultConfig);
        this.ignoreErrorPriority = (boolean)defaultConfig.getValue("error_collector.ignoreErrorPriority", (Object)Boolean.TRUE);
        final TransactionTraceService ttService = ServiceFactory.getTransactionTraceService();
        this.ttEnabled = ttService.isEnabled();
        this.counts = new TransactionCounts(defaultConfig);
        final MapMaker factory = new MapMaker().initialCapacity(8).concurrencyLevel(4);
        this.internalParameters = new LazyMapImpl<String, Object>(factory);
        this.prefixedAgentAttributes = new LazyMapImpl<String, Map<String, String>>(factory);
        this.agentAttributes = new LazyMapImpl<String, Object>(factory);
        this.intrinsicAttributes = new LazyMapImpl<String, Object>(factory);
        this.userAttributes = new LazyMapImpl<String, Object>(factory);
        this.errorAttributes = new LazyMapImpl<String, String>(factory);
        this.insights = ServiceFactory.getServiceManager().getInsights().getTransactionInsights(defaultConfig);
        this.contextToTracer = new LazyMapImpl<Object, Tracer>(new MapMaker().initialCapacity(25).concurrencyLevel(16));
        this.timedOutKeys = new LazyMapImpl<Object, Tracer>(factory);
        this.runningChildren = new LazyMapImpl<Object, TransactionActivity>(factory);
        this.finishedChildren = new LazyMapImpl<Object, TransactionActivity>(factory);
    }
    
    private void postConstruct() {
        final TransactionActivity txa = TransactionActivity.create(this, this.nextActivityId.getAndIncrement());
        txa.setContext(txa);
        this.initialActivity = txa;
        this.legacyState.boundThreads.add(Thread.currentThread().getId());
        synchronized (this.lock) {
            this.runningChildren.put(txa.getContext(), txa);
        }
    }
    
    private static long getGCTime() {
        long gcTime = 0L;
        for (final GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            gcTime += gcBean.getCollectionTime();
        }
        return gcTime;
    }
    
    private boolean initializeTransactionNamingEnabled(final AgentConfig config) {
        if (!config.isAutoTransactionNamingEnabled()) {
            return false;
        }
        final IRPMService rpmService = this.getRPMService();
        if (rpmService == null) {
            return true;
        }
        final String transactionNamingScheme = this.getRPMService().getTransactionNamingScheme();
        return "framework" != transactionNamingScheme;
    }
    
    public MetricAggregator getMetricAggregator() {
        return this.metricAggregator;
    }
    
    public IAgent getAgent() {
        return this.agent;
    }
    
    public Object getLock() {
        return this.lock;
    }
    
    public String getGuid() {
        return this.guid;
    }
    
    public AgentConfig getAgentConfig() {
        AgentConfig config = null;
        do {
            final AppNameAndConfig nc = this.appNameAndConfig.get();
            config = nc.config;
            if (config == null) {
                config = ServiceFactory.getConfigService().getAgentConfig(nc.name.getName());
                if (this.appNameAndConfig.compareAndSet(nc, new AppNameAndConfig(nc.name, config))) {
                    continue;
                }
                config = null;
            }
        } while (config == null);
        return config;
    }
    
    public long getWallClockStartTimeMs() {
        this.captureWallClockStartTime();
        return this.wallClockStartTimeMs;
    }
    
    private void captureWallClockStartTime() {
        if (this.wallClockStartTimeMs == 0L) {
            this.wallClockStartTimeMs = System.currentTimeMillis();
        }
    }
    
    public Map<String, Object> getInternalParameters() {
        return this.internalParameters;
    }
    
    public Map<String, Map<String, String>> getPrefixedAgentAttributes() {
        return this.prefixedAgentAttributes;
    }
    
    public Map<String, Object> getUserAttributes() {
        return this.userAttributes;
    }
    
    public Map<String, Object> getAgentAttributes() {
        return this.agentAttributes;
    }
    
    public Map<String, Object> getIntrinsicAttributes() {
        return this.intrinsicAttributes;
    }
    
    public Map<String, String> getErrorAttributes() {
        return this.errorAttributes;
    }
    
    public Insights getInsightsData() {
        return this.insights;
    }
    
    public TransactionTracerConfig getTransactionTracerConfig() {
        if (this.dispatcher == null) {
            return this.getAgentConfig().getTransactionTracerConfig();
        }
        return this.dispatcher.getTransactionTracerConfig();
    }
    
    public CrossProcessConfig getCrossProcessConfig() {
        return this.getAgentConfig().getCrossProcessConfig();
    }
    
    public boolean setTransactionName(final TransactionNamePriority namePriority, final boolean override, final String category, final String... parts) {
        return this.setTransactionName(com.newrelic.agent.bridge.TransactionNamePriority.convert(namePriority), override, category, parts);
    }
    
    public boolean setTransactionName(final com.newrelic.agent.bridge.TransactionNamePriority namePriority, final boolean override, final String category, final String... parts) {
        return this.getRootTransaction().doSetTransactionName(namePriority, override, category, parts);
    }
    
    @Deprecated
    public boolean isTransactionNameSet() {
        return this.getRootTransaction().getPriorityTransactionName().getPriority().isGreaterThan(com.newrelic.agent.bridge.TransactionNamePriority.NONE);
    }
    
    private boolean doSetTransactionName(final com.newrelic.agent.bridge.TransactionNamePriority namePriority, final boolean override, final String category, final String... parts) {
        if (namePriority.isLessThan(com.newrelic.agent.bridge.TransactionNamePriority.CUSTOM_HIGH) && !this.isTransactionNamingEnabled()) {
            return false;
        }
        final String name = Strings.join('/', parts);
        if (this.dispatcher == null) {
            if (Agent.LOG.isFinestEnabled()) {
                Agent.LOG.finest(MessageFormat.format("Unable to set the transaction name to \"{0}\" - no transaction", name));
            }
            return false;
        }
        final TransactionNamingPolicy policy = override ? TransactionNamingPolicy.getSameOrHigherPriorityTransactionNamingPolicy() : TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
        return policy.setTransactionName(this, name, category, namePriority);
    }
    
    public PriorityTransactionName getPriorityTransactionName() {
        return this.priorityTransactionName;
    }
    
    public void freezeTransactionName() {
        synchronized (this.lock) {
            if (this.priorityTransactionName.isFrozen()) {
                return;
            }
            this.dispatcher.setTransactionName();
            this.renameTransaction();
            this.priorityTransactionName = this.priorityTransactionName.freeze();
        }
    }
    
    private void renameTransaction() {
        if (Agent.LOG.isFinestEnabled()) {
            this.threadAssertion();
        }
        final String appName = this.getApplicationName();
        final Normalizer metricDataNormalizer = ServiceFactory.getNormalizationService().getMetricNormalizer(appName);
        String txName = metricDataNormalizer.normalize(this.priorityTransactionName.getName());
        final Normalizer txNormalizer = ServiceFactory.getNormalizationService().getTransactionNormalizer(appName);
        txName = txNormalizer.normalize(txName);
        if (txName == null) {
            this.setIgnore(true);
            return;
        }
        if (!txName.equals(this.priorityTransactionName.getName())) {
            this.setPriorityTransactionNameLocked(PriorityTransactionName.create(txName, this.isWebTransaction() ? "Web" : "Other", com.newrelic.agent.bridge.TransactionNamePriority.REQUEST_URI));
        }
    }
    
    public boolean conditionalSetPriorityTransactionName(final TransactionNamingPolicy policy, final String name, final String category, final com.newrelic.agent.bridge.TransactionNamePriority priority) {
        synchronized (this.lock) {
            if (policy.canSetTransactionName(this, priority)) {
                Agent.LOG.log(Level.FINER, "Setting transaction name to \"{0}\" for transaction {1}", new Object[] { name, this });
                return this.setPriorityTransactionNameLocked(policy.getPriorityTransactionName(this, name, category, priority));
            }
            Agent.LOG.log(Level.FINER, "Not setting the transaction name to  \"{0}\" for transaction {1}: a higher priority name is already in place. Current transaction name is {2}", new Object[] { name, this, this.getTransactionName() });
            return false;
        }
    }
    
    public boolean setPriorityTransactionName(final PriorityTransactionName ptn) {
        synchronized (this.lock) {
            return this.setPriorityTransactionNameLocked(ptn);
        }
    }
    
    private boolean setPriorityTransactionNameLocked(final PriorityTransactionName ptn) {
        if (Agent.LOG.isFinestEnabled()) {
            this.threadAssertion();
        }
        if (ptn == null) {
            return false;
        }
        this.priorityTransactionName = ptn;
        return true;
    }
    
    public SqlTracerListener getSqlTracerListener() {
        synchronized (this.lock) {
            if (this.sqlTracerListener == null) {
                final String appName = this.getApplicationName();
                this.sqlTracerListener = ServiceFactory.getSqlTraceService().getSqlTracerListener(appName);
            }
            return this.sqlTracerListener;
        }
    }
    
    public TransactionCache getTransactionCache() {
        return this.getTransactionActivity().getTransactionCache();
    }
    
    public ConnectionCache getConnectionCache() {
        if (this.connectionCache == null) {
            synchronized (this.lock) {
                if (this.connectionCache == null) {
                    this.connectionCache = new ConnectionCache();
                }
            }
        }
        return this.connectionCache;
    }
    
    public boolean isStarted() {
        return this.getDispatcher() != null;
    }
    
    public boolean isFinished() {
        synchronized (this.lock) {
            return this.isStarted() && this.runningChildren.isEmpty() && this.contextToTracer.isEmpty();
        }
    }
    
    public boolean isInProgress() {
        synchronized (this.lock) {
            return this.isStarted() && (!this.runningChildren.isEmpty() || !this.contextToTracer.isEmpty());
        }
    }
    
    public Dispatcher getDispatcher() {
        return this.dispatcher;
    }
    
    public long getExternalTime() {
        if (this.dispatcher instanceof WebRequestDispatcher) {
            return ((WebRequestDispatcher)this.dispatcher).getQueueTime();
        }
        return 0L;
    }
    
    public Tracer getRootTracer() {
        return this.rootTracer;
    }
    
    public List<Tracer> getAllTracers() {
        this.transactionState.mergeAsyncTracers();
        return this.getTracers();
    }
    
    public List<Tracer> getTracers() {
        return new TracerList(this.getRootTracer(), this.getFinishedChildren());
    }
    
    public TransactionActivity getTransactionActivity() {
        if (this.legacyState.boundThreads.size() == 0) {
            return this.initialActivity;
        }
        final TransactionActivity result = TransactionActivity.get();
        if (result == null) {
            throw new IllegalStateException("TransactionActivity is gone");
        }
        return result;
    }
    
    @Deprecated
    public TransactionActivity getInitialTransactionActivity() {
        return this.initialActivity;
    }
    
    void activityStarted(final TransactionActivity activity) {
        Agent.LOG.log(Level.FINER, "activity {0} starting", new Object[] { activity });
        this.startTransactionIfBeginning(activity.getRootTracer());
    }
    
    public void startTransactionIfBeginning(final Tracer tracer) {
        if (tracer instanceof TransactionActivityInitiator) {
            Agent.LOG.log(Level.FINER, "Starting transaction {0}", new Object[] { this });
            this.captureWallClockStartTime();
            if (ServiceFactory.getTransactionTraceService().isEnabled()) {
                final AgentConfig defaultConfig = ServiceFactory.getConfigService().getDefaultAgentConfig();
                this.startGCTimeInMillis = (defaultConfig.getTransactionTracerConfig().isGCTimeEnabled() ? getGCTime() : -1L);
            }
            else {
                this.startGCTimeInMillis = -1L;
            }
            if (this.rootTracer == null) {
                this.rootTracer = tracer;
            }
            if (this.transactionTime == null) {
                this.transactionTime = new TransactionTimer(tracer.getStartTime());
                Agent.LOG.log(Level.FINER, "Set timer for transaction {0}", new Object[] { this });
            }
            if (this.dispatcher == null) {
                this.setDispatcher(((TransactionActivityInitiator)tracer).createDispatcher());
            }
        }
    }
    
    public void setDispatcher(final Dispatcher dispatcher) {
        Agent.LOG.log(Level.FINER, "Set dispatcher {0} for transaction {1}", new Object[] { dispatcher, this });
        this.dispatcher = dispatcher;
    }
    
    public TransactionTimer getTransactionTimer() {
        return this.transactionTime;
    }
    
    private void finishTransaction() {
        final String requestURI = (this.dispatcher == null) ? "No Dispatcher Defined" : this.dispatcher.getUri();
        final IRPMService rpmService = this.getRPMService();
        this.beforeSendResponseHeaders();
        this.freezeTransactionName();
        if (this.ignore || this.finishedChildren.isEmpty()) {
            Agent.LOG.log(Level.FINE, "Ignoring transaction {0}", new Object[] { this });
            return;
        }
        if (this.isAsyncTransaction()) {
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                final String msg = MessageFormat.format("Async transaction {1} finished {0}", requestURI, this);
                Agent.LOG.finest(msg);
            }
            return;
        }
        final TransactionStats transactionStats = this.transactionFinishedActivityMerging();
        this.recordFinalGCTime(transactionStats);
        this.addUnStartedAsyncKeys(transactionStats);
        final String txName = this.priorityTransactionName.getName();
        this.dispatcher.transactionFinished(txName, transactionStats);
        if (Agent.LOG.isFinerEnabled()) {
            Agent.LOG.log(Level.FINER, "Transaction {2} finished {0}ms {1}", new Object[] { this.transactionTime.getResponseTimeInMilliseconds(), requestURI, this });
        }
        if (!ServiceFactory.getServiceManager().isStarted()) {
            return;
        }
        if (Agent.LOG.isFinerEnabled()) {
            Agent.LOG.log(Level.FINER, "Transaction name for {0} is {1}", new Object[] { requestURI, txName });
            if (this.isAutoAppNamingEnabled()) {
                Agent.LOG.log(Level.FINER, "Application name for {0} is {1}", new Object[] { txName, rpmService.getApplicationName() });
            }
        }
        final TransactionTracerConfig ttConfig = this.getTransactionTracerConfig();
        final TransactionCounts rootCounts = this.getTransactionCounts();
        if (rootCounts.isOverTracerSegmentLimit()) {
            this.getIntrinsicAttributes().put("segment_clamp", rootCounts.getSegmentCount());
        }
        if (rootCounts.isOverTransactionSize()) {
            this.getIntrinsicAttributes().put("size_limit", "The transaction size limit was reached");
        }
        int count = rootCounts.getStackTraceCount();
        if (count >= ttConfig.getMaxStackTraces()) {
            this.getIntrinsicAttributes().put("stack_trace_clamp", count);
        }
        count = rootCounts.getExplainPlanCount();
        if (count >= ttConfig.getMaxExplainPlans()) {
            this.getIntrinsicAttributes().put("explain_plan_clamp", count);
        }
        if (this.getInboundHeaderState().isTrustedCatRequest()) {
            final String id = this.getInboundHeaderState().getClientCrossProcessId();
            this.getIntrinsicAttributes().put("client_cross_process_id", id);
        }
        final String referrerGuid = this.getInboundHeaderState().getReferrerGuid();
        if (referrerGuid != null) {
            this.getIntrinsicAttributes().put("referring_transaction_guid", referrerGuid);
        }
        final String tripId = this.getCrossProcessTransactionState().getTripId();
        if (tripId != null) {
            this.getIntrinsicAttributes().put("trip_id", tripId);
            final int pathHash = this.getCrossProcessTransactionState().generatePathHash();
            this.getIntrinsicAttributes().put("path_hash", ServiceUtils.intToHexString(pathHash));
        }
        if (this.isSynthetic()) {
            Agent.LOG.log(Level.FINEST, "Completing Synthetics transaction for monitor {0}", new Object[] { this.getInboundHeaderState().getSyntheticsMonitorId() });
            this.getIntrinsicAttributes().put("synthetics_resource_id", this.getInboundHeaderState().getSyntheticsResourceId());
            this.getIntrinsicAttributes().put("synthetics_monitor_id", this.getInboundHeaderState().getSyntheticsMonitorId());
            this.getIntrinsicAttributes().put("synthetics_job_id", this.getInboundHeaderState().getSyntheticsJobId());
        }
        final String displayHost = (String)this.getAgentConfig().getValue("process_host.display_name", (Object)null);
        if (displayHost != null) {
            this.getAgentAttributes().put("host.displayName", displayHost);
        }
        final String instanceName = ServiceFactory.getEnvironmentService().getEnvironment().getAgentIdentity().getInstanceName();
        if (instanceName != null) {
            this.getAgentAttributes().put("process.instanceName", instanceName);
        }
        this.getAgentAttributes().put("jvm.thread_name", Thread.currentThread().getName());
        final TransactionData transactionData = new TransactionData(this, rootCounts.getTransactionSize());
        ServiceFactory.getTransactionService().processTransaction(transactionData, transactionStats);
    }
    
    private TransactionStats transactionFinishedActivityMerging() {
        TransactionStats transactionStats = null;
        long totalCpuTime;
        if (!this.isTransactionTraceEnabled() || this.getRunningDurationInNanos() <= this.getTransactionTracerConfig().getTransactionThresholdInNanos()) {
            totalCpuTime = -1L;
        }
        else {
            final Object val = this.getIntrinsicAttributes().remove("cpu_time");
            if (val != null && val instanceof Long) {
                totalCpuTime = (long)val;
            }
            else {
                totalCpuTime = 0L;
            }
        }
        for (final TransactionActivity kid : this.getFinishedChildren()) {
            if (transactionStats == null) {
                transactionStats = kid.getTransactionStats();
            }
            else {
                final TransactionStats stats = kid.getTransactionStats();
                transactionStats.getScopedStats().mergeStats(stats.getScopedStats());
                transactionStats.getUnscopedStats().mergeStats(stats.getUnscopedStats());
            }
            if (kid.getRootTracer() != null) {
                final Tracer rootTracer = kid.getRootTracer();
                this.transactionTime.incrementTransactionTotalTime(rootTracer.getDuration());
                this.transactionTime.setTransactionEndTimeIfLonger(rootTracer.getEndTime());
                if (Agent.LOG.isFinestEnabled()) {
                    final Map<String, Object> tracerAtts = rootTracer.getAttributes();
                    if (tracerAtts != null && !tracerAtts.isEmpty()) {
                        Agent.LOG.log(Level.FINEST, "Tracer Attributes for {0} are {1}", new Object[] { rootTracer, tracerAtts });
                    }
                }
            }
            if (totalCpuTime > -1L) {
                final long tempCpuTime = (kid.getTotalCpuTime() > -1L) ? kid.getTotalCpuTime() : -1L;
                if (tempCpuTime == -1L) {
                    totalCpuTime = -1L;
                }
                else {
                    totalCpuTime += tempCpuTime;
                }
            }
        }
        if (totalCpuTime > 0L) {
            this.getIntrinsicAttributes().put("cpu_time", totalCpuTime);
        }
        return transactionStats;
    }
    
    public synchronized void addTotalCpuTimeForLegacy(final long time) {
        final Object val = this.getIntrinsicAttributes().remove("cpu_time");
        long totalCpuTime;
        if (val != null && val instanceof Long) {
            totalCpuTime = (long)val;
        }
        else {
            totalCpuTime = 0L;
        }
        if (totalCpuTime != -1L) {
            totalCpuTime += time;
        }
        this.getIntrinsicAttributes().put("cpu_time", totalCpuTime);
    }
    
    public void recordFinalGCTime(final TransactionStats stats) {
        if (this.isTransactionTraceEnabled() && this.getRunningDurationInNanos() > this.getTransactionTracerConfig().getTransactionThresholdInNanos()) {
            Long totalGCTime = this.getIntrinsicAttributes().get("gc_time");
            if (totalGCTime == null && this.startGCTimeInMillis > -1L) {
                final long gcTime = getGCTime();
                if (gcTime != this.startGCTimeInMillis) {
                    totalGCTime = gcTime - this.startGCTimeInMillis;
                    this.getIntrinsicAttributes().put("gc_time", totalGCTime);
                    stats.getUnscopedStats().getResponseTimeStats("GC/cumulative").recordResponseTime(totalGCTime, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
    
    private void addUnStartedAsyncKeys(final TransactionStats stats) {
        if (!this.timedOutKeys.isEmpty()) {
            stats.getUnscopedStats().getStats("Supportability/Timeout/startAsyncNotCalled").setCallCount(this.timedOutKeys.size());
        }
        if (this.isTransactionTraceEnabled()) {
            for (final Map.Entry<Object, Tracer> current : this.timedOutKeys.entrySet()) {
                final Object val = current.getValue().getAttribute("unstarted_async_activity");
                Map<String, Integer> keys;
                if (val == null) {
                    keys = (Map<String, Integer>)Maps.newHashMap();
                }
                else {
                    keys = (Map<String, Integer>)val;
                }
                final String classType = current.getKey().getClass().toString();
                Integer count = keys.get(classType);
                if (count == null) {
                    count = 1;
                }
                else {
                    ++count;
                }
                keys.put(classType, count);
                current.getValue().setAttribute("unstarted_async_activity", keys);
            }
        }
    }
    
    public boolean isTransactionTraceEnabled() {
        return this.ttEnabled;
    }
    
    public boolean isAutoAppNamingEnabled() {
        return this.autoAppNamingEnabled;
    }
    
    public boolean isTransactionNamingEnabled() {
        return this.transactionNamingEnabled;
    }
    
    public boolean isWebTransaction() {
        return this.dispatcher != null && this.dispatcher.isWebTransaction();
    }
    
    public boolean isAsyncTransaction() {
        return this.dispatcher != null && this.dispatcher.isAsyncTransaction();
    }
    
    public boolean isSynthetic() {
        return this.getInboundHeaderState().isTrustedSyntheticsRequest();
    }
    
    public void provideHeaders(final InboundHeaders headers) {
        if (headers != null) {
            final String encodingKey = this.getCrossProcessConfig().getEncodingKey();
            this.provideRawHeaders((InboundHeaders)new DeobfuscatedInboundHeaders(headers, encodingKey));
        }
    }
    
    public void provideRawHeaders(final InboundHeaders headers) {
        if (headers != null) {
            synchronized (this.lock) {
                this.providedHeaders = headers;
            }
        }
    }
    
    public InboundHeaderState getInboundHeaderState() {
        final Transaction tx = this.getRootTransaction();
        synchronized (tx.lock) {
            if (tx.inboundHeaderState == null) {
                InboundHeaders requestHeaders = getRequestHeaders(tx);
                if (requestHeaders == null) {
                    requestHeaders = ((this.providedHeaders == null) ? null : this.providedHeaders);
                }
                try {
                    tx.inboundHeaderState = new InboundHeaderState(tx, requestHeaders);
                }
                catch (RuntimeException rex) {
                    Agent.LOG.log(Level.FINEST, "Unable to parse inbound headers", rex);
                    tx.inboundHeaderState = new InboundHeaderState(tx, null);
                }
            }
            return tx.inboundHeaderState;
        }
    }
    
    static InboundHeaders getRequestHeaders(final Transaction tx) {
        if (tx.dispatcher != null && tx.dispatcher.getRequest() != null) {
            return (InboundHeaders)new DeobfuscatedInboundHeaders((InboundHeaders)tx.dispatcher.getRequest(), tx.getCrossProcessConfig().getEncodingKey());
        }
        return null;
    }
    
    public IRPMService getRPMService() {
        return ServiceFactory.getRPMServiceManager().getOrCreateRPMService(this.getPriorityApplicationName());
    }
    
    public static void clearTransaction() {
        final Transaction tx = Transaction.transactionHolder.get();
        if (tx != null) {
            tx.legacyState.boundThreads.remove(Thread.currentThread().getId());
        }
        Transaction.transactionHolder.remove();
        TransactionActivity.clear();
    }
    
    public static void setTransaction(final Transaction tx) {
        tx.legacyState.boundThreads.add(Thread.currentThread().getId());
        TransactionActivity.set(tx.initialActivity);
        Transaction.transactionHolder.set(tx);
    }
    
    public static Transaction getTransaction() {
        return getTransaction(true);
    }
    
    static Transaction getTransaction(final boolean createIfNotExists) {
        Transaction tx = Transaction.transactionHolder.get();
        if (tx == null && createIfNotExists && !(Thread.currentThread() instanceof ThreadService.AgentThread)) {
            if (ServiceFactory.getServiceManager().getCircuitBreakerService().isTripped()) {
                return getOrCreateDummyTransaction();
            }
            try {
                tx = new Transaction();
                tx.postConstruct();
                ServiceFactory.getTransactionService().addTransaction(tx);
                tx.legacyState.boundThreads.add(Thread.currentThread().getId());
                Transaction.transactionHolder.set(tx);
            }
            catch (RuntimeException rex) {
                Agent.LOG.log(Level.FINEST, (Throwable)rex, "while creating Transaction", new Object[0]);
                TransactionActivity.clear();
                throw rex;
            }
        }
        return tx;
    }
    
    protected static synchronized Transaction getOrCreateDummyTransaction() {
        if (Transaction.dummyTransaction == null) {
            Transaction.dummyTransaction = new DummyTransaction();
        }
        return Transaction.dummyTransaction;
    }
    
    @Deprecated
    public void setNormalizedUri(final String normalizedUri) {
        synchronized (this.lock) {
            if (normalizedUri == null || normalizedUri.length() == 0) {
                return;
            }
            final TransactionNamingPolicy policy = TransactionNamingPolicy.getSameOrHigherPriorityTransactionNamingPolicy();
            if (Agent.LOG.isLoggable(Level.FINER) && policy.canSetTransactionName(this, com.newrelic.agent.bridge.TransactionNamePriority.CUSTOM_HIGH)) {
                final String msg = MessageFormat.format("Setting transaction name to normalized URI \"{0}\" for transaction {1}", normalizedUri, this);
                Agent.LOG.finer(msg);
            }
            policy.setTransactionName(this, normalizedUri, "NormalizedUri", com.newrelic.agent.bridge.TransactionNamePriority.CUSTOM_HIGH);
            this.normalizedUri = normalizedUri;
        }
    }
    
    @Deprecated
    public String getNormalizedUri() {
        synchronized (this.lock) {
            return this.normalizedUri;
        }
    }
    
    public Throwable getReportError() {
        return ServletUtils.getReportError(this.throwable);
    }
    
    public int getStatus() {
        return this.getWebResponse().getStatus();
    }
    
    public String getStatusMessage() {
        return this.getWebResponse().getStatusMessage();
    }
    
    public void freezeStatus() {
        this.getWebResponse().freezeStatus();
    }
    
    public void setThrowable(final Throwable throwable, final TransactionErrorPriority priority) {
        if (throwable == null) {
            return;
        }
        if (TransactionActivity.get() != this.initialActivity && priority != TransactionErrorPriority.API) {
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, "Non-API call to setThrowable from asynchronous activity ignored: {0} with priority {1}", new Object[] { throwable, priority });
            }
            return;
        }
        if (Agent.LOG.isFinerEnabled() && !this.ignoreErrorPriority) {
            Agent.LOG.log(Level.FINER, "Attempting to set throwable in transaction: {0} having priority {1} with priority {2}", new Object[] { throwable.getClass().getName(), this.throwablePriority, priority });
        }
        if (this.ignoreErrorPriority || priority.updateCurrentPriority(this.throwablePriority)) {
            Agent.LOG.log(Level.FINER, "Set throwable {0} in transaction {1}", new Object[] { throwable.getClass().getName(), this });
            this.throwable = throwable;
        }
    }
    
    public boolean isIgnore() {
        return this.ignore;
    }
    
    public void ignore() {
        this.setIgnore(true);
    }
    
    public void setIgnore(final boolean ignore) {
        if (this.dispatcher != null) {
            synchronized (this.lock) {
                this.ignore = ignore;
                for (final TransactionActivity runningChild : this.runningChildren.values()) {
                    runningChild.setOwningTransactionIsIgnored(true);
                }
                for (final TransactionActivity finishedChild : this.finishedChildren.values()) {
                    finishedChild.setOwningTransactionIsIgnored(true);
                }
            }
        }
        else {
            Agent.LOG.log(Level.FINEST, "setIgnore called outside of an open transaction");
        }
    }
    
    public void ignoreApdex() {
        if (this.dispatcher != null) {
            this.dispatcher.setIgnoreApdex(true);
        }
        else {
            Agent.LOG.finer("ignoreApdex invoked with no transaction");
        }
    }
    
    public TransactionCounts getTransactionCounts() {
        return this.getRootTransaction().counts;
    }
    
    public boolean shouldGenerateTransactionSegment() {
        return this.ttEnabled && this.getTransactionCounts().shouldGenerateTransactionSegment();
    }
    
    public DatabaseStatementParser getDatabaseStatementParser() {
        synchronized (this.lock) {
            if (this.databaseStatementParser == null) {
                this.databaseStatementParser = this.createDatabaseStatementParser();
            }
            return this.databaseStatementParser;
        }
    }
    
    private DatabaseStatementParser createDatabaseStatementParser() {
        return new CachingDatabaseStatementParser(ServiceFactory.getDatabaseService().getDatabaseStatementParser());
    }
    
    public BrowserTransactionState getBrowserTransactionState() {
        synchronized (this.lock) {
            if (this.browserTransactionState == null) {
                this.browserTransactionState = BrowserTransactionStateImpl.create(this);
            }
            return this.browserTransactionState;
        }
    }
    
    public CrossProcessState getCrossProcessState() {
        return (CrossProcessState)this.getCrossProcessTransactionState();
    }
    
    public CrossProcessTransactionState getCrossProcessTransactionState() {
        final Transaction tx = this.getRootTransaction();
        synchronized (tx) {
            if (tx.crossProcessTransactionState == null) {
                tx.crossProcessTransactionState = CrossProcessTransactionStateImpl.create(tx);
            }
            return tx.crossProcessTransactionState;
        }
    }
    
    public TransactionState getTransactionState() {
        return this.transactionState;
    }
    
    public void setTransactionState(final TransactionState transactionState) {
        Agent.disableFastPath();
        this.transactionState = transactionState;
    }
    
    public Transaction getRootTransaction() {
        if (this.legacyState.rootTransaction == null) {
            return this;
        }
        return this.legacyState.rootTransaction;
    }
    
    public void setRootTransaction(final Transaction tx) {
        if (this != tx) {
            this.legacyState.rootTransaction = tx;
        }
    }
    
    public void beforeSendResponseHeaders() {
        this.getCrossProcessTransactionState().writeResponseHeaders();
    }
    
    public WebResponse getWebResponse() {
        if (this.dispatcher instanceof WebResponse) {
            return (WebResponse)this.dispatcher;
        }
        return Transaction.DEFAULT_RESPONSE;
    }
    
    public void convertToWebTransaction() {
        if (!this.isWebTransaction()) {
            this.setDispatcher(new WebRequestDispatcher(Transaction.DUMMY_REQUEST, Transaction.DUMMY_RESPONSE, this));
        }
    }
    
    public void requestInitialized(final Request request, Response response) {
        Agent.LOG.log(Level.FINEST, "Request initialized: {0}", new Object[] { request.getRequestURI() });
        synchronized (this.requestStateChangeLock) {
            if (this.isFinished()) {
                return;
            }
            if (this.dispatcher == null) {
                final ExitTracer tracer = AgentBridge.instrumentation.createTracer((Object)null, Transaction.REQUEST_INITIALIZED_CLASS_SIGNATURE_ID, (String)null, 14);
                if (tracer != null) {
                    if (response == null) {
                        response = Transaction.DUMMY_RESPONSE;
                    }
                    this.setDispatcher(new WebRequestDispatcher(request, response, this));
                }
            }
            else {
                Agent.LOG.finer("requestInitialized(): transaction already started.");
            }
        }
    }
    
    public void requestDestroyed() {
        Agent.LOG.log(Level.FINEST, "Request destroyed");
        synchronized (this.requestStateChangeLock) {
            if (!this.isInProgress()) {
                return;
            }
            final Tracer rootTracer = this.getTransactionActivity().getRootTracer();
            final Tracer lastTracer = this.getTransactionActivity().getLastTracer();
            if (lastTracer != null && rootTracer == lastTracer) {
                lastTracer.finish(177, (Object)null);
            }
            else {
                Agent.LOG.log(Level.FINER, "Inconsistent state!  tracer != last tracer for {0} ({1} != {2})", new Object[] { this, rootTracer, lastTracer });
            }
        }
    }
    
    public boolean isWebRequestSet() {
        return this.dispatcher instanceof WebRequestDispatcher && !Transaction.DUMMY_REQUEST.equals(this.dispatcher.getRequest());
    }
    
    public boolean isWebResponseSet() {
        return this.dispatcher instanceof WebRequestDispatcher && !Transaction.DUMMY_RESPONSE.equals(this.dispatcher.getResponse());
    }
    
    public void setWebRequest(final Request request) {
        NewRelic.getAgent().getLogger().log(Level.FINEST, "setWebRequest invoked", new Object[0]);
        if (!(this.dispatcher instanceof WebRequestDispatcher)) {
            this.setDispatcher(new WebRequestDispatcher(request, Transaction.DUMMY_RESPONSE, getTransaction()));
        }
        else {
            this.dispatcher.setRequest(request);
        }
    }
    
    public void setWebResponse(final Response response) {
        NewRelic.getAgent().getLogger().log(Level.FINEST, "setWebResponse invoked", new Object[0]);
        if (this.dispatcher instanceof WebRequestDispatcher) {
            this.dispatcher.setResponse(response);
        }
    }
    
    public static boolean isDummyRequest(final Request request) {
        return request == Transaction.DUMMY_REQUEST;
    }
    
    public String getApplicationName() {
        return this.getPriorityApplicationName().getName();
    }
    
    public PriorityApplicationName getPriorityApplicationName() {
        return this.appNameAndConfig.get().name;
    }
    
    public void setApplicationName(final ApplicationNamePriority priority, final String appName) {
        this.setApplicationName(priority, appName, false);
    }
    
    private void setApplicationName(final ApplicationNamePriority priority, final String appName, final boolean override) {
        if (appName == null || appName.length() == 0) {
            return;
        }
        final ApplicationNamingPolicy policy = override ? SameOrHigherPriorityApplicationNamingPolicy.getInstance() : HigherPriorityApplicationNamingPolicy.getInstance();
        synchronized (this.lock) {
            if (policy.canSetApplicationName(this, priority)) {
                final String name = stripLeadingForwardSlash(appName);
                final PriorityApplicationName pan = PriorityApplicationName.create(name, priority);
                this.setPriorityApplicationName(pan);
            }
        }
    }
    
    private static String stripLeadingForwardSlash(final String appName) {
        final String FORWARD_SLASH = "/";
        if (appName.length() > 1 && appName.startsWith("/")) {
            return appName.substring(1, appName.length());
        }
        return appName;
    }
    
    private void setPriorityApplicationName(final PriorityApplicationName pan) {
        if (pan == null || pan.equals(this.getPriorityApplicationName())) {
            return;
        }
        Agent.LOG.log(Level.FINE, "Set application name to {0}", new Object[] { pan.getName() });
        this.appNameAndConfig.set(new AppNameAndConfig(pan, null));
    }
    
    public long getRunningDurationInNanos() {
        if (this.dispatcher == null) {
            return 0L;
        }
        return this.transactionTime.getRunningDurationInNanos();
    }
    
    public void saveMessageParameters(final Map<String, String> parameters) {
        MessagingUtil.recordParameters(this, parameters);
    }
    
    public boolean registerAsyncActivity(final Object activityContext) {
        boolean result = false;
        synchronized (this.lock) {
            if (this.isInProgress()) {
                final Tracer t = this.getTransactionActivity().getLastTracer();
                if (t == null) {
                    Agent.LOG.log(Level.FINE, "Parent tracer not found. Not registering async activity context {0} with transaction {1}", new Object[] { activityContext, this });
                }
                else if (!ServiceFactory.getAsyncTxService().putIfAbsent(activityContext, this)) {
                    Agent.LOG.log(Level.FINER, "Key already in use. Not registering async activity context {0} with transaction {1}", new Object[] { activityContext, this });
                }
                else {
                    this.contextToTracer.put(activityContext, t);
                    Agent.LOG.log(Level.FINER, "Registering async activity context {0} with transaction {1}", new Object[] { activityContext, this });
                    result = true;
                }
            }
        }
        return result;
    }
    
    public boolean startAsyncActivity(final Object activityContext) {
        boolean result = false;
        synchronized (this.lock) {
            if (this.isInProgress()) {
                final Transaction transaction = ServiceFactory.getAsyncTxService().extractIfPresent(activityContext);
                if (transaction == null) {
                    Agent.LOG.log(Level.FINER, "startAsyncActivity(): there is no transaction associated with context {0}", new Object[] { activityContext });
                }
                else if (transaction == this) {
                    Agent.LOG.log(Level.FINER, "Transaction started in current running transaction {0} for context {1}", new Object[] { transaction, activityContext });
                    this.contextToTracer.remove(activityContext);
                    if (this.isIgnore()) {
                        this.getTransactionActivity().setOwningTransactionIsIgnored(this.isIgnore());
                    }
                }
                else {
                    result = true;
                    this.migrate(transaction, activityContext);
                    Agent.LOG.log(Level.FINER, "startAsyncActivity(): activity {0} (context {1}) unbound from transaction {2} and bound to {3}", new Object[] { this.getTransactionActivity(), activityContext, this, transaction });
                }
            }
            else {
                Agent.LOG.log(Level.FINER, "startAsyncActivity must be called within a transaction.");
            }
        }
        return result;
    }
    
    public void timeoutAsyncActivity(final Object activityContext) {
        synchronized (this.lock) {
            final Tracer tracer = this.contextToTracer.remove(activityContext);
            if (tracer != null) {
                this.timedOutKeys.put(activityContext, tracer);
                this.checkFinishTransaction();
            }
        }
    }
    
    public boolean ignoreAsyncActivity(final Object activityContext) {
        final String baseMessage = "ignoreAsyncActivity({0}): {1}";
        boolean result = true;
        synchronized (this.lock) {
            String detailMessage = null;
            final Transaction tx = ServiceFactory.getAsyncTxService().extractIfPresent(activityContext);
            if (tx != null) {
                final Tracer t = tx.contextToTracer.remove(activityContext);
                if (t == null) {
                    Agent.LOG.log(Level.FINER, "ignoreAsyncActivity({0}): {1}", new Object[] { activityContext, "tracer not found" });
                }
                detailMessage = "pending activity ignored.";
            }
            else if (this.runningChildren.containsKey(activityContext)) {
                final TransactionActivity txa = this.runningChildren.remove(activityContext);
                txa.setToIgnore();
                detailMessage = "running activity ignored.";
            }
            else if (this.finishedChildren.containsKey(activityContext)) {
                final TransactionActivity txa = this.finishedChildren.remove(activityContext);
                txa.setToIgnore();
                detailMessage = "finished activity ignored.";
            }
            else {
                detailMessage = "activity not found.";
                result = false;
            }
            Agent.LOG.log(Level.FINE, "ignoreAsyncActivity({0}): {1}", new Object[] { activityContext, detailMessage });
            this.checkFinishTransaction();
        }
        return result;
    }
    
    private void migrate(final Transaction newTrans, final Object context) {
        if (Agent.LOG.isFinestEnabled()) {
            this.threadAssertion();
        }
        if (this == newTrans) {
            return;
        }
        final TransactionActivity activity = this.getTransactionActivity();
        activity.setOwningTransactionIsIgnored(newTrans.isIgnore());
        final Tracer tracer = newTrans.contextToTracer.remove(context);
        activity.startAsyncActivity(context, newTrans, this.nextActivityId.getAndIncrement(), tracer);
        newTrans.runningChildren.put(context, activity);
        Transaction.transactionHolder.set(newTrans);
        newTrans.legacyState.boundThreads.add(Thread.currentThread().getId());
        final PriorityApplicationName pan = this.getPriorityApplicationName();
        if (pan != PriorityApplicationName.NONE) {
            newTrans.setApplicationName(pan.getPriority(), pan.getName(), true);
        }
        final PriorityTransactionName ptn = this.getPriorityTransactionName();
        if (ptn != PriorityTransactionName.NONE) {
            newTrans.setTransactionName(ptn.getPriority(), true, ptn.getCategory(), ptn.getName());
        }
        newTrans.getInternalParameters().putAll(this.getInternalParameters());
        newTrans.getPrefixedAgentAttributes().putAll(this.getPrefixedAgentAttributes());
        newTrans.getAgentAttributes().putAll(this.getAgentAttributes());
        newTrans.getIntrinsicAttributes().putAll(this.getIntrinsicAttributes());
        newTrans.getUserAttributes().putAll(this.getUserAttributes());
        newTrans.getErrorAttributes().putAll(this.getErrorAttributes());
    }
    
    public Set<TransactionActivity> getFinishedChildren() {
        synchronized (this.lock) {
            return new HashSet<TransactionActivity>(this.finishedChildren.values());
        }
    }
    
    public void activityFinished(final TransactionActivity activity, final Tracer tracer, final int opcode) {
        Agent.LOG.log(Level.FINER, "Activity {0} with context {1} finished with opcode {2} in transaction {3}.", new Object[] { activity, activity.getContext(), opcode, this });
        synchronized (this.lock) {
            try {
                final Object context = activity.getContext();
                if (this.runningChildren.remove(context) == null) {
                    Agent.LOG.log(Level.FINE, "The completing activity {0} was not in the running list for transaction {1}", new Object[] { activity, this });
                }
                else {
                    this.finishedChildren.put(context, activity);
                }
                this.checkFinishTransaction();
            }
            finally {
                this.legacyState.boundThreads.remove(Thread.currentThread().getId());
                Transaction.transactionHolder.remove();
            }
        }
    }
    
    public void activityFailed(final TransactionActivity activity, final int opcode) {
        Agent.LOG.log(Level.FINER, "activity {0} FAILED with opcode {1}", new Object[] { activity, opcode });
        synchronized (this.lock) {
            try {
                this.runningChildren.remove(activity.getContext());
                this.finishedChildren.remove(activity.getContext());
                this.checkFinishTransaction();
            }
            finally {
                this.legacyState.boundThreads.remove(Thread.currentThread().getId());
                Transaction.transactionHolder.remove();
            }
        }
    }
    
    private void checkFinishTransaction() {
        if (Agent.LOG.isFinestEnabled()) {
            this.threadAssertion();
        }
        if (this.runningChildren.isEmpty() && this.contextToTracer.isEmpty()) {
            this.finishTransaction();
        }
    }
    
    private final void threadAssertion() {
        if (Agent.LOG.isFinestEnabled() && !Thread.holdsLock(this.lock)) {
            Agent.LOG.log(Level.FINEST, "Thread assertion failed!", new Exception("Thread assertion failed!").fillInStackTrace());
        }
    }
    
    private String getTransactionName() {
        final String fullName = this.getPriorityTransactionName().getName();
        final String category = this.getPriorityTransactionName().getCategory();
        final String prefix = this.getPriorityTransactionName().getPrefix();
        final String txnNamePrefix = prefix + '/' + category + '/';
        if (fullName != null && fullName.startsWith(txnNamePrefix)) {
            return fullName.substring(txnNamePrefix.length(), fullName.length());
        }
        return fullName;
    }
    
    static {
        REQUEST_INITIALIZED_CLASS_SIGNATURE = new ClassMethodSignature("javax.servlet.ServletRequestListener", "requestInitialized", "(Ljavax/servlet/ServletRequestEvent;)V");
        REQUEST_INITIALIZED_CLASS_SIGNATURE_ID = ClassMethodSignatures.get().add(Transaction.REQUEST_INITIALIZED_CLASS_SIGNATURE);
        transactionHolder = new ThreadLocal<Transaction>() {
            public void remove() {
                ServiceFactory.getTransactionService().removeTransaction();
                super.remove();
            }
            
            public void set(final Transaction value) {
                super.set(value);
                ServiceFactory.getTransactionService().addTransaction(value);
            }
        };
        DEFAULT_RESPONSE = (WebResponse)new WebResponse() {
            public void setStatusMessage(final String message) {
            }
            
            public void setStatus(final int statusCode) {
            }
            
            public int getStatus() {
                return 0;
            }
            
            public String getStatusMessage() {
                return null;
            }
            
            public void freezeStatus() {
            }
        };
        DUMMY_REQUEST = (Request)new Request() {
            public String[] getParameterValues(final String name) {
                return null;
            }
            
            public Enumeration<?> getParameterNames() {
                return null;
            }
            
            public Object getAttribute(final String name) {
                return null;
            }
            
            public String getRequestURI() {
                return "/";
            }
            
            public String getRemoteUser() {
                return null;
            }
            
            public String getHeader(final String name) {
                return null;
            }
            
            public String getCookieValue(final String name) {
                return null;
            }
            
            public HeaderType getHeaderType() {
                return HeaderType.HTTP;
            }
        };
        DUMMY_RESPONSE = (Response)new Response() {
            public int getStatus() throws Exception {
                return 0;
            }
            
            public String getStatusMessage() throws Exception {
                return null;
            }
            
            public void setHeader(final String name, final String value) {
            }
            
            public String getContentType() {
                return null;
            }
            
            public HeaderType getHeaderType() {
                return HeaderType.HTTP;
            }
        };
    }
    
    protected class AppNameAndConfig
    {
        final PriorityApplicationName name;
        final AgentConfig config;
        
        AppNameAndConfig(final PriorityApplicationName name, final AgentConfig config) {
            this.name = name;
            this.config = config;
        }
    }
    
    private static class LegacyState
    {
        volatile Transaction rootTransaction;
        final Set<Long> boundThreads;
        
        LegacyState() {
            final MapMaker factory = new MapMaker().initialCapacity(8).concurrencyLevel(4);
            this.boundThreads = Sets.newSetFromMap(new LazyMapImpl<Long, Boolean>(factory));
        }
    }
}
