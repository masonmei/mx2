// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.TracerFactory;
import java.net.URL;
import com.newrelic.agent.tracers.MetricNameFormatWithHost;
import com.newrelic.agent.deps.com.google.common.cache.Cache;
import com.newrelic.agent.instrumentation.pointcuts.database.ConnectionFactory;
import java.sql.Connection;
import java.io.IOException;
import java.io.Writer;
import com.newrelic.agent.stats.ApdexStatsImpl;
import com.newrelic.agent.stats.ResponseTimeStatsImpl;
import com.newrelic.agent.stats.StatsImpl;
import com.newrelic.agent.metric.MetricIdRegistry;
import com.newrelic.agent.normalization.Normalizer;
import com.newrelic.agent.stats.ApdexStats;
import com.newrelic.agent.stats.ResponseTimeStats;
import com.newrelic.agent.stats.Stats;
import com.newrelic.agent.stats.StatsBase;
import com.newrelic.agent.stats.SimpleStatsEngine;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.api.agent.OutboundHeaders;
import java.sql.ResultSetMetaData;
import com.newrelic.agent.database.ParsedDatabaseStatement;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.stats.AbstractMetricAggregator;
import com.newrelic.api.agent.ApplicationNamePriority;
import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import com.newrelic.agent.bridge.WebResponse;
import com.newrelic.agent.bridge.CrossProcessState;
import com.newrelic.agent.browser.BrowserTransactionState;
import com.newrelic.agent.database.DatabaseStatementParser;
import com.newrelic.agent.stats.TransactionStats;
import java.util.List;
import com.newrelic.agent.dispatchers.Dispatcher;
import com.newrelic.agent.transaction.ConnectionCache;
import com.newrelic.agent.transaction.TransactionCache;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import com.newrelic.agent.transaction.PriorityTransactionName;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.agent.config.CrossProcessConfig;
import com.newrelic.agent.config.TransactionTracerConfig;
import com.newrelic.agent.trace.TransactionGuidFactory;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.sql.NopSqlTracerListener;
import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.application.PriorityApplicationName;
import com.newrelic.api.agent.MetricAggregator;
import java.util.Set;
import com.newrelic.agent.transaction.TransactionCounts;
import com.newrelic.agent.sql.SqlTracerListener;
import com.newrelic.agent.transaction.TransactionTimer;
import java.util.concurrent.atomic.AtomicReference;
import com.newrelic.api.agent.Insights;
import java.util.Map;

public class DummyTransaction extends Transaction
{
    private final IAgent agent;
    private final String guid;
    private final Map<String, String> dummyMap;
    private final Map<String, Object> dummyObjectMap;
    private final Map<String, Map<String, String>> dummyStringMap;
    private final Object lock;
    private final Insights insights;
    private final AtomicReference<AppNameAndConfig> appNameAndConfig;
    private final TracerList tracerList;
    private final TransactionTimer timer;
    private final InboundHeaderState inboundHeaderState;
    private final SqlTracerListener sqlTracerListener;
    private final boolean autoAppNamingEnabled;
    private final TransactionCounts txnCounts;
    private final Set<TransactionActivity> finishedChildren;
    private static final MetricAggregator metricAggregator;
    
    protected DummyTransaction() {
        this.dummyMap = new DummyMap<String, String>();
        this.dummyObjectMap = new DummyMap<String, Object>();
        this.dummyStringMap = new DummyMap<String, Map<String, String>>();
        this.lock = new Object();
        this.insights = (Insights)new DummyInsights();
        this.appNameAndConfig = new AtomicReference<AppNameAndConfig>(new AppNameAndConfig(this, PriorityApplicationName.NONE, null));
        this.tracerList = new TracerList(null, new DummySet<TransactionActivity>());
        this.timer = new TransactionTimer(0L);
        this.inboundHeaderState = new InboundHeaderState(null, null);
        this.sqlTracerListener = new NopSqlTracerListener();
        this.finishedChildren = new DummySet<TransactionActivity>();
        this.agent = ServiceFactory.getAgent();
        this.guid = TransactionGuidFactory.generateGuid();
        final AgentConfig defaultConfig = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.autoAppNamingEnabled = defaultConfig.isAutoAppNamingEnabled();
        this.txnCounts = new TransactionCounts(defaultConfig);
    }
    
    public MetricAggregator getMetricAggregator() {
        return DummyTransaction.metricAggregator;
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
                if (this.appNameAndConfig.compareAndSet(nc, new AppNameAndConfig(this, nc.name, config))) {
                    continue;
                }
                config = null;
            }
        } while (config == null);
        return config;
    }
    
    public long getWallClockStartTimeMs() {
        return 0L;
    }
    
    public Map<String, Object> getInternalParameters() {
        return this.dummyObjectMap;
    }
    
    public Map<String, Map<String, String>> getPrefixedAgentAttributes() {
        return this.dummyStringMap;
    }
    
    public Map<String, Object> getUserAttributes() {
        return this.dummyObjectMap;
    }
    
    public Map<String, Object> getAgentAttributes() {
        return this.dummyObjectMap;
    }
    
    public Map<String, Object> getIntrinsicAttributes() {
        return this.dummyObjectMap;
    }
    
    public Map<String, String> getErrorAttributes() {
        return this.dummyMap;
    }
    
    public Insights getInsightsData() {
        return this.insights;
    }
    
    public TransactionTracerConfig getTransactionTracerConfig() {
        return this.getAgentConfig().getTransactionTracerConfig();
    }
    
    public CrossProcessConfig getCrossProcessConfig() {
        return this.getAgentConfig().getCrossProcessConfig();
    }
    
    public boolean setTransactionName(final TransactionNamePriority namePriority, final boolean override, final String category, final String... parts) {
        return false;
    }
    
    public boolean setTransactionName(final com.newrelic.agent.bridge.TransactionNamePriority namePriority, final boolean override, final String category, final String... parts) {
        return false;
    }
    
    public boolean isTransactionNameSet() {
        return false;
    }
    
    public PriorityTransactionName getPriorityTransactionName() {
        return PriorityTransactionName.NONE;
    }
    
    public void freezeTransactionName() {
    }
    
    public boolean conditionalSetPriorityTransactionName(final TransactionNamingPolicy policy, final String name, final String category, final com.newrelic.agent.bridge.TransactionNamePriority priority) {
        return false;
    }
    
    public boolean setPriorityTransactionName(final PriorityTransactionName ptn) {
        return false;
    }
    
    public SqlTracerListener getSqlTracerListener() {
        return this.sqlTracerListener;
    }
    
    public TransactionCache getTransactionCache() {
        return DummyTransactionCache.INSTANCE;
    }
    
    public ConnectionCache getConnectionCache() {
        return DummyConnectionCache.INSTANCE;
    }
    
    public boolean isStarted() {
        return false;
    }
    
    public boolean isFinished() {
        return true;
    }
    
    public boolean isInProgress() {
        return false;
    }
    
    public Dispatcher getDispatcher() {
        return null;
    }
    
    public long getExternalTime() {
        return 0L;
    }
    
    public Tracer getRootTracer() {
        return null;
    }
    
    public List<Tracer> getAllTracers() {
        return this.getTracers();
    }
    
    public List<Tracer> getTracers() {
        return this.tracerList;
    }
    
    public TransactionActivity getTransactionActivity() {
        return DummyTransactionActivity.INSTANCE;
    }
    
    public TransactionActivity getInitialTransactionActivity() {
        return null;
    }
    
    void activityStarted(final TransactionActivity activity) {
    }
    
    public void startTransactionIfBeginning(final Tracer tracer) {
    }
    
    public void setDispatcher(final Dispatcher dispatcher) {
    }
    
    public TransactionTimer getTransactionTimer() {
        return this.timer;
    }
    
    public void addTotalCpuTimeForLegacy(final long time) {
    }
    
    public void recordFinalGCTime(final TransactionStats stats) {
    }
    
    public boolean isTransactionTraceEnabled() {
        return false;
    }
    
    public boolean isAutoAppNamingEnabled() {
        return this.autoAppNamingEnabled;
    }
    
    public boolean isTransactionNamingEnabled() {
        return false;
    }
    
    public boolean isWebTransaction() {
        return false;
    }
    
    public boolean isAsyncTransaction() {
        return false;
    }
    
    public boolean isSynthetic() {
        return false;
    }
    
    public void provideHeaders(final InboundHeaders headers) {
    }
    
    public void provideRawHeaders(final InboundHeaders headers) {
    }
    
    public InboundHeaderState getInboundHeaderState() {
        return this.inboundHeaderState;
    }
    
    public IRPMService getRPMService() {
        return ServiceFactory.getRPMServiceManager().getOrCreateRPMService(this.getPriorityApplicationName());
    }
    
    public void setNormalizedUri(final String normalizedUri) {
    }
    
    public String getNormalizedUri() {
        return null;
    }
    
    public Throwable getReportError() {
        return null;
    }
    
    public int getStatus() {
        return 0;
    }
    
    public String getStatusMessage() {
        return null;
    }
    
    public void freezeStatus() {
    }
    
    public void setThrowable(final Throwable throwable, final TransactionErrorPriority priority) {
    }
    
    public boolean isIgnore() {
        return true;
    }
    
    public void ignore() {
    }
    
    public void setIgnore(final boolean ignore) {
    }
    
    public void ignoreApdex() {
    }
    
    public TransactionCounts getTransactionCounts() {
        return this.txnCounts;
    }
    
    public boolean shouldGenerateTransactionSegment() {
        return false;
    }
    
    public DatabaseStatementParser getDatabaseStatementParser() {
        return DummyDatabaseStatementParser.INSTANCE;
    }
    
    public BrowserTransactionState getBrowserTransactionState() {
        return null;
    }
    
    public CrossProcessState getCrossProcessState() {
        return (CrossProcessState)DummyCrossProcessState.INSTANCE;
    }
    
    public CrossProcessTransactionState getCrossProcessTransactionState() {
        return DummyCrossProcessState.INSTANCE;
    }
    
    public TransactionState getTransactionState() {
        return DummyTransactionState.INSTANCE;
    }
    
    public void setTransactionState(final TransactionState transactionState) {
    }
    
    public Transaction getRootTransaction() {
        return this;
    }
    
    public void setRootTransaction(final Transaction tx) {
    }
    
    public void beforeSendResponseHeaders() {
    }
    
    public WebResponse getWebResponse() {
        return DummyTransaction.DEFAULT_RESPONSE;
    }
    
    public void convertToWebTransaction() {
    }
    
    public void requestInitialized(final Request request, final Response response) {
    }
    
    public void requestDestroyed() {
    }
    
    public boolean isWebRequestSet() {
        return false;
    }
    
    public boolean isWebResponseSet() {
        return false;
    }
    
    public void setWebRequest(final Request request) {
    }
    
    public void setWebResponse(final Response response) {
    }
    
    public String getApplicationName() {
        return this.getPriorityApplicationName().getName();
    }
    
    public PriorityApplicationName getPriorityApplicationName() {
        return PriorityApplicationName.NONE;
    }
    
    public void setApplicationName(final ApplicationNamePriority priority, final String appName) {
    }
    
    public long getRunningDurationInNanos() {
        return 0L;
    }
    
    public void saveMessageParameters(final Map<String, String> parameters) {
    }
    
    public boolean registerAsyncActivity(final Object activityContext) {
        return false;
    }
    
    public boolean startAsyncActivity(final Object activityContext) {
        return false;
    }
    
    public void timeoutAsyncActivity(final Object activityContext) {
    }
    
    public boolean ignoreAsyncActivity(final Object activityContext) {
        return false;
    }
    
    public Set<TransactionActivity> getFinishedChildren() {
        return this.finishedChildren;
    }
    
    public void activityFinished(final TransactionActivity activity, final Tracer tracer, final int opcode) {
    }
    
    public void activityFailed(final TransactionActivity activity, final int opcode) {
    }
    
    static {
        metricAggregator = (MetricAggregator)new AbstractMetricAggregator() {
            protected void doRecordResponseTimeMetric(final String name, final long totalTime, final long exclusiveTime, final TimeUnit timeUnit) {
            }
            
            protected void doRecordMetric(final String name, final float value) {
            }
            
            protected void doIncrementCounter(final String name, final int count) {
            }
        };
    }
    
    static final class DummyMap<K, V> implements Map<K, V>
    {
        public int size() {
            return 0;
        }
        
        public boolean isEmpty() {
            return true;
        }
        
        public boolean containsKey(final Object key) {
            return false;
        }
        
        public boolean containsValue(final Object value) {
            return false;
        }
        
        public V get(final Object key) {
            return null;
        }
        
        public V put(final K key, final V value) {
            return null;
        }
        
        public V remove(final Object key) {
            return null;
        }
        
        public void putAll(final Map<? extends K, ? extends V> m) {
        }
        
        public void clear() {
        }
        
        public Set<K> keySet() {
            return Collections.emptySet();
        }
        
        public Collection<V> values() {
            return (Collection<V>)Collections.emptySet();
        }
        
        public Set<Entry<K, V>> entrySet() {
            return Collections.emptySet();
        }
    }
    
    static final class DummySet<E> implements Set<E>
    {
        private final Set<E> object;
        
        DummySet() {
            this.object = new HashSet<E>();
        }
        
        public int size() {
            return 0;
        }
        
        public boolean isEmpty() {
            return true;
        }
        
        public boolean contains(final Object o) {
            return false;
        }
        
        public Iterator<E> iterator() {
            return this.object.iterator();
        }
        
        public Object[] toArray() {
            return this.object.toArray();
        }
        
        public <T> T[] toArray(final T[] a) {
            return this.object.toArray(a);
        }
        
        public boolean add(final E e) {
            return false;
        }
        
        public boolean remove(final Object o) {
            return false;
        }
        
        public boolean containsAll(final Collection<?> c) {
            return false;
        }
        
        public boolean addAll(final Collection<? extends E> c) {
            return false;
        }
        
        public boolean retainAll(final Collection<?> c) {
            return false;
        }
        
        public boolean removeAll(final Collection<?> c) {
            return false;
        }
        
        public void clear() {
        }
    }
    
    static final class DummyDatabaseStatementParser implements DatabaseStatementParser
    {
        static final DatabaseStatementParser INSTANCE;
        private static final ParsedDatabaseStatement parsedDatabaseStatement;
        
        public ParsedDatabaseStatement getParsedDatabaseStatement(final String statement, final ResultSetMetaData resultSetMetaData) {
            return DummyDatabaseStatementParser.parsedDatabaseStatement;
        }
        
        static {
            INSTANCE = new DummyDatabaseStatementParser();
            parsedDatabaseStatement = new ParsedDatabaseStatement(null, null, false);
        }
    }
    
    final class DummyInsights implements Insights
    {
        public void recordCustomEvent(final String eventType, final Map<String, Object> attributes) {
        }
    }
    
    static final class DummyCrossProcessState implements CrossProcessTransactionState
    {
        public static final CrossProcessTransactionState INSTANCE;
        
        public void processOutboundRequestHeaders(final OutboundHeaders outboundHeaders) {
        }
        
        public void processOutboundResponseHeaders(final OutboundHeaders outboundHeaders, final long contentLength) {
        }
        
        public void processInboundResponseHeaders(final InboundHeaders inboundHeaders, final TracedMethod tracer, final String host, final String uri, final boolean addRollupMetric) {
        }
        
        public String getRequestMetadata() {
            return null;
        }
        
        public void processRequestMetadata(final String requestMetadata) {
        }
        
        public String getResponseMetadata() {
            return null;
        }
        
        public void processResponseMetadata(final String responseMetadata) {
        }
        
        public void writeResponseHeaders() {
        }
        
        public String getTripId() {
            return "";
        }
        
        public int generatePathHash() {
            return 0;
        }
        
        public String getAlternatePathHashes() {
            return "";
        }
        
        static {
            INSTANCE = new DummyCrossProcessState();
        }
    }
    
    static final class DummyTransactionActivity extends TransactionActivity
    {
        public static final TransactionActivity INSTANCE;
        
        public Object getContext() {
            return null;
        }
        
        public void setContext(final Object context) {
        }
        
        public TransactionStats getTransactionStats() {
            return DummyTransactionStats.INSTANCE;
        }
        
        public List<Tracer> getTracers() {
            return Collections.emptyList();
        }
        
        public long getTotalCpuTime() {
            return 0L;
        }
        
        public void setToIgnore() {
        }
        
        void setOwningTransactionIsIgnored(final boolean newState) {
        }
        
        public Tracer tracerStarted(final Tracer tracer) {
            return tracer;
        }
        
        public void tracerFinished(final Tracer tracer, final int opcode) {
        }
        
        public boolean isStarted() {
            return true;
        }
        
        public boolean isFlyweight() {
            return false;
        }
        
        public void recordCpu() {
        }
        
        public void addTracer(final Tracer tracer) {
        }
        
        public boolean checkTracerStart() {
            return false;
        }
        
        public Tracer getLastTracer() {
            return null;
        }
        
        public TracedMethod startFlyweightTracer() {
            return null;
        }
        
        public void finishFlyweightTracer(final TracedMethod parent, final long startInNanos, final long finishInNanos, final String className, final String methodName, final String methodDesc, final String metricName, final String[] rollupMetricNames) {
        }
        
        public void startAsyncActivity(final Object context, final Transaction transaction, final int activityId, final Tracer parentTracer) {
        }
        
        public Tracer getRootTracer() {
            return null;
        }
        
        public TransactionCache getTransactionCache() {
            return Transaction.getOrCreateDummyTransaction().getTransactionCache();
        }
        
        public Transaction getTransaction() {
            return Transaction.getOrCreateDummyTransaction();
        }
        
        public int hashCode() {
            return 0;
        }
        
        static {
            INSTANCE = new DummyTransactionActivity();
        }
    }
    
    static final class DummyTransactionStats extends TransactionStats
    {
        public static final TransactionStats INSTANCE;
        static final SimpleStatsEngine stats;
        
        public SimpleStatsEngine getUnscopedStats() {
            return DummyTransactionStats.stats;
        }
        
        public SimpleStatsEngine getScopedStats() {
            return DummyTransactionStats.stats;
        }
        
        public int getSize() {
            return 0;
        }
        
        public String toString() {
            return "";
        }
        
        static {
            INSTANCE = new DummyTransactionStats();
            stats = new DummySimpleStatsEngine();
        }
    }
    
    static final class DummySimpleStatsEngine extends SimpleStatsEngine
    {
        static final Map<String, StatsBase> statsMap;
        static final DummyStats stat;
        static final DummyResponseTimeStats responseTimeStat;
        static final DummyApdexStat apdexStat;
        
        public Map<String, StatsBase> getStatsMap() {
            return DummySimpleStatsEngine.statsMap;
        }
        
        public Stats getStats(final String metricName) {
            return DummySimpleStatsEngine.stat;
        }
        
        public ResponseTimeStats getResponseTimeStats(final String metric) {
            return DummySimpleStatsEngine.responseTimeStat;
        }
        
        public void recordEmptyStats(final String metricName) {
        }
        
        public ApdexStats getApdexStats(final String metricName) {
            return DummySimpleStatsEngine.apdexStat;
        }
        
        public void mergeStats(final SimpleStatsEngine other) {
        }
        
        public void clear() {
        }
        
        public int getSize() {
            return 0;
        }
        
        public List<MetricData> getMetricData(final Normalizer metricNormalizer, final MetricIdRegistry metricIdRegistry, final String scope) {
            return Collections.emptyList();
        }
        
        public String toString() {
            return "";
        }
        
        static {
            statsMap = new DummyMap<String, StatsBase>();
            stat = new DummyStats();
            responseTimeStat = new DummyResponseTimeStats();
            apdexStat = new DummyApdexStat();
        }
    }
    
    static final class DummyStats extends StatsImpl
    {
        public DummyStats() {
        }
        
        public DummyStats(final int count, final float total, final float minValue, final float maxValue, final double sumOfSquares) {
        }
        
        public Object clone() throws CloneNotSupportedException {
            return this;
        }
        
        public String toString() {
            return "";
        }
        
        public void recordDataPoint(final float value) {
        }
        
        public boolean hasData() {
            return false;
        }
        
        public void reset() {
        }
        
        public float getTotal() {
            return 0.0f;
        }
        
        public float getTotalExclusiveTime() {
            return 0.0f;
        }
        
        public float getMinCallTime() {
            return 0.0f;
        }
        
        public float getMaxCallTime() {
            return 0.0f;
        }
        
        public double getSumOfSquares() {
            return 0.0;
        }
        
        public void merge(final StatsBase statsObj) {
        }
        
        public void incrementCallCount(final int value) {
        }
        
        public void incrementCallCount() {
        }
        
        public int getCallCount() {
            return 0;
        }
        
        public void setCallCount(final int count) {
        }
    }
    
    static final class DummyResponseTimeStats extends ResponseTimeStatsImpl
    {
        public Object clone() throws CloneNotSupportedException {
            return this;
        }
        
        public void recordResponseTime(final long responseTime, final TimeUnit timeUnit) {
        }
        
        public void recordResponseTime(final long responseTime, final long exclusiveTime, final TimeUnit timeUnit) {
        }
        
        public void recordResponseTimeInNanos(final long responseTime) {
        }
        
        public void recordResponseTimeInNanos(final long responseTime, final long exclusiveTime) {
        }
        
        public boolean hasData() {
            return false;
        }
        
        public void reset() {
        }
        
        public float getTotal() {
            return 0.0f;
        }
        
        public float getTotalExclusiveTime() {
            return 0.0f;
        }
        
        public float getMaxCallTime() {
            return 0.0f;
        }
        
        public float getMinCallTime() {
            return 0.0f;
        }
        
        public double getSumOfSquares() {
            return 0.0;
        }
        
        public void recordResponseTime(final int count, final long totalTime, final long minTime, final long maxTime, final TimeUnit unit) {
        }
        
        public String toString() {
            return "";
        }
        
        public void incrementCallCount(final int value) {
        }
        
        public void incrementCallCount() {
        }
        
        public int getCallCount() {
            return 0;
        }
        
        public void setCallCount(final int count) {
        }
    }
    
    static final class DummyApdexStat extends ApdexStatsImpl
    {
        public Object clone() throws CloneNotSupportedException {
            return this;
        }
        
        public String toString() {
            return "";
        }
        
        public void recordApdexFrustrated() {
        }
        
        public int getApdexSatisfying() {
            return 0;
        }
        
        public int getApdexTolerating() {
            return 0;
        }
        
        public int getApdexFrustrating() {
            return 0;
        }
        
        public void recordApdexResponseTime(final long responseTimeMillis, final long apdexTInMillis) {
        }
        
        public boolean hasData() {
            return false;
        }
        
        public void reset() {
        }
        
        public void writeJSONString(final Writer writer) throws IOException {
        }
        
        public void merge(final StatsBase statsObj) {
        }
    }
    
    static final class DummyConnectionCache extends ConnectionCache
    {
        static final ConnectionCache INSTANCE;
        
        public void putConnectionFactory(final Connection key, final ConnectionFactory val) {
        }
        
        public long getConnectionFactoryCacheSize() {
            return 0L;
        }
        
        public ConnectionFactory removeConnectionFactory(final Connection key) {
            return null;
        }
        
        public Cache<Connection, ConnectionFactory> getConnectionFactoryCache() {
            return null;
        }
        
        public void clear() {
        }
        
        static {
            INSTANCE = new DummyConnectionCache();
        }
    }
    
    static final class DummyTransactionCache extends TransactionCache
    {
        public static final TransactionCache INSTANCE;
        
        public MetricNameFormatWithHost getMetricNameFormatWithHost(final Object key) {
            return null;
        }
        
        public void putMetricNameFormatWithHost(final Object key, final MetricNameFormatWithHost val) {
        }
        
        public Object removeSolrResponseBuilderParamName() {
            return null;
        }
        
        public void putSolrResponseBuilderParamName(final Object val) {
        }
        
        public URL getURL(final Object key) {
            return null;
        }
        
        public void putURL(final Object key, final URL val) {
        }
        
        static {
            INSTANCE = new DummyTransactionCache();
        }
    }
    
    static final class DummyTransactionState implements TransactionState
    {
        public static final TransactionState INSTANCE;
        
        public Tracer getTracer(final Transaction tx, final TracerFactory tracerFactory, final ClassMethodSignature sig, final Object obj, final Object... args) {
            return null;
        }
        
        public Tracer getTracer(final Transaction tx, final String tracerFactoryName, final ClassMethodSignature sig, final Object obj, final Object... args) {
            return null;
        }
        
        public Tracer getTracer(final Transaction tx, final Object invocationTarget, final ClassMethodSignature sig, final String metricName, final int flags) {
            return null;
        }
        
        public boolean finish(final Transaction tx, final Tracer tracer) {
            return false;
        }
        
        public void resume() {
        }
        
        public void suspend() {
        }
        
        public void suspendRootTracer() {
        }
        
        public void complete() {
        }
        
        public void asyncJobStarted(final TransactionHolder job) {
        }
        
        public void asyncJobFinished(final TransactionHolder job) {
        }
        
        public void asyncTransactionStarted(final Transaction tx, final TransactionHolder txHolder) {
        }
        
        public void asyncTransactionFinished(final TransactionActivity txa) {
        }
        
        public void mergeAsyncTracers() {
        }
        
        public Tracer getRootTracer() {
            return null;
        }
        
        public void asyncJobInvalidate(final TransactionHolder job) {
        }
        
        public void setInvalidateAsyncJobs(final boolean invalidate) {
        }
        
        static {
            INSTANCE = new DummyTransactionState();
        }
    }
}
