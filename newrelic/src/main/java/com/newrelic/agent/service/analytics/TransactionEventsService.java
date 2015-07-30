// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.analytics;

import com.newrelic.agent.stats.StatsBase;
import com.newrelic.agent.stats.AbstractStats;
import com.newrelic.agent.stats.CountStats;
import com.newrelic.agent.transaction.PriorityTransactionName;
import java.util.Map;
import com.newrelic.agent.attributes.AttributesUtils;
import java.util.concurrent.ExecutionException;
import java.text.MessageFormat;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.TransactionData;
import com.newrelic.agent.Agent;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.deps.com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.com.google.common.cache.CacheBuilder;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.deps.com.google.common.cache.LoadingCache;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.TransactionListener;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.Service;
import com.newrelic.agent.service.AbstractService;

public class TransactionEventsService extends AbstractService implements Service, HarvestListener, TransactionListener, AgentConfigListener
{
    private final boolean enabled;
    private final int maxSamplesStored;
    private final ConcurrentHashMap<String, ReserviorSampledArrayList<TransactionEvent>> reservoirForApp;
    private final ConcurrentHashMap<String, FixedSizeArrayList<TransactionEvent>> syntheticsListForApp;
    private final ConcurrentMap<String, Boolean> isEnabledForApp;
    final ArrayDeque<FixedSizeArrayList<TransactionEvent>> pendingSyntheticsArrays;
    static final int MAX_UNSENT_SYNTHETICS_HOLDERS = 25;
    static final int MAX_SYNTHETIC_EVENTS_PER_APP = 200;
    private final LoadingCache<String, String> transactionNameCache;
    
    public TransactionEventsService() {
        super(TransactionEventsService.class.getSimpleName());
        this.reservoirForApp = new ConcurrentHashMap<String, ReserviorSampledArrayList<TransactionEvent>>();
        this.syntheticsListForApp = new ConcurrentHashMap<String, FixedSizeArrayList<TransactionEvent>>();
        this.isEnabledForApp = new ConcurrentHashMap<String, Boolean>();
        this.pendingSyntheticsArrays = new ArrayDeque<FixedSizeArrayList<TransactionEvent>>();
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.maxSamplesStored = TransactionEventsConfigUtils.getMaxSamplesStored(config);
        this.enabled = TransactionEventsConfigUtils.isTransactionEventsEnabled(config, this.maxSamplesStored);
        this.isEnabledForApp.put(config.getApplicationName(), this.enabled);
        this.transactionNameCache = CacheBuilder.newBuilder().maximumSize(this.maxSamplesStored).expireAfterAccess(5L, TimeUnit.MINUTES).build((CacheLoader<? super String, String>)new CacheLoader<String, String>() {
            public String load(final String key) throws Exception {
                return key;
            }
        });
    }
    
    public final boolean isEnabled() {
        return this.enabled;
    }
    
    protected void doStart() throws Exception {
        if (this.enabled) {
            ServiceFactory.getHarvestService().addHarvestListener(this);
            ServiceFactory.getTransactionService().addTransactionListener(this);
            ServiceFactory.getConfigService().addIAgentConfigListener(this);
        }
    }
    
    protected void doStop() throws Exception {
        ServiceFactory.getHarvestService().removeHarvestListener(this);
        ServiceFactory.getTransactionService().removeTransactionListener(this);
        ServiceFactory.getConfigService().removeIAgentConfigListener(this);
        this.reservoirForApp.clear();
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
        this.beforeHarvestSynthetics(appName, statsEngine);
        final List<TransactionEvent> reservoirToSend = this.reservoirForApp.put(appName, new ReserviorSampledArrayList<TransactionEvent>(this.maxSamplesStored));
        if (reservoirToSend != null && reservoirToSend.size() > 0) {
            try {
                ServiceFactory.getRPMService(appName).sendAnalyticsEvents((Collection<TransactionEvent>)Collections.unmodifiableList((List<?>)reservoirToSend));
            }
            catch (Exception e) {
                Agent.LOG.fine("Unable to send events for regular transactions. This operation will be retried.");
                final ReserviorSampledArrayList<TransactionEvent> currentReservoir = this.reservoirForApp.get(appName);
                currentReservoir.addAll((Collection<?>)reservoirToSend);
            }
        }
    }
    
    private void beforeHarvestSynthetics(final String appName, final StatsEngine statsEngine) {
        final FixedSizeArrayList<TransactionEvent> current = this.syntheticsListForApp.put(appName, new FixedSizeArrayList<TransactionEvent>(200));
        if (current != null && current.size() > 0) {
            if (this.pendingSyntheticsArrays.size() < 25) {
                this.pendingSyntheticsArrays.add(current);
            }
            else {
                Agent.LOG.fine("Some synthetic transaction events were discarded.");
            }
        }
        final int maxToSend = 5;
        for (int nSent = 0; nSent < 5; ++nSent) {
            final FixedSizeArrayList<TransactionEvent> toSend = this.pendingSyntheticsArrays.poll();
            if (toSend == null) {
                break;
            }
            try {
                ServiceFactory.getRPMService(appName).sendAnalyticsEvents((Collection<TransactionEvent>)Collections.unmodifiableList((List<?>)toSend));
                ++nSent;
            }
            catch (Exception e) {
                Agent.LOG.fine("Unable to send events for synthetic transactions. This operation will be retried.");
                this.pendingSyntheticsArrays.add(toSend);
                break;
            }
        }
    }
    
    public void afterHarvest(final String appName) {
    }
    
    private boolean getIsEnabledForApp(final AgentConfig config, final String currentAppName) {
        Boolean appEnabled = this.isEnabledForApp.get(currentAppName);
        if (appEnabled == null) {
            appEnabled = TransactionEventsConfigUtils.isTransactionEventsEnabled(config, TransactionEventsConfigUtils.getMaxSamplesStored(config));
            this.isEnabledForApp.put(currentAppName, appEnabled);
        }
        return appEnabled;
    }
    
    public void dispatcherTransactionFinished(final TransactionData transactionData, final TransactionStats transactionStats) {
        final String name = transactionData.getApplicationName();
        if (!this.getIsEnabledForApp(transactionData.getAgentConfig(), name)) {
            this.reservoirForApp.remove(name);
            return;
        }
        boolean persisted = false;
        if (transactionData.isSyntheticTransaction()) {
            FixedSizeArrayList<TransactionEvent> currentSyntheticsList;
            for (currentSyntheticsList = this.syntheticsListForApp.get(name); currentSyntheticsList == null; currentSyntheticsList = this.syntheticsListForApp.get(name)) {
                this.syntheticsListForApp.putIfAbsent(name, new FixedSizeArrayList<TransactionEvent>(200));
            }
            persisted = currentSyntheticsList.add(this.createEvent(transactionData, transactionStats));
            final String msg = MessageFormat.format("Added Synthetics transaction event: {0}", transactionData);
            Agent.LOG.finest(msg);
        }
        if (!persisted) {
            ReserviorSampledArrayList<TransactionEvent> currentReservoir;
            for (currentReservoir = this.reservoirForApp.get(name); currentReservoir == null; currentReservoir = this.reservoirForApp.get(name)) {
                this.reservoirForApp.putIfAbsent(name, new ReserviorSampledArrayList<TransactionEvent>(this.maxSamplesStored));
            }
            final Integer slot = currentReservoir.getSlot();
            if (slot != null) {
                currentReservoir.set(slot, this.createEvent(transactionData, transactionStats));
            }
        }
    }
    
    private TransactionEvent createEvent(final TransactionData transactionData, final TransactionStats transactionStats) {
        final long startTime = transactionData.getWallClockStartTimeMs();
        String metricName = transactionData.getBlameOrRootMetricName();
        try {
            metricName = this.transactionNameCache.get(metricName);
        }
        catch (ExecutionException e) {
            Agent.LOG.finest("Error fetching cached transaction name: " + e.toString());
        }
        final long durationInNanos = transactionData.getDuration();
        final Integer port = ServiceFactory.getEnvironmentService().getEnvironment().getAgentIdentity().getServerPort();
        String subType = "Web";
        if (!transactionData.isWebTransaction()) {
            final PriorityTransactionName transactionName = transactionData.getPriorityTransactionName();
            final String otherCategory = transactionName.getCategory();
            if (otherCategory != null) {
                subType = otherCategory;
            }
        }
        final TransactionEvent event = new TransactionEvent(transactionData.getApplicationName(), subType, startTime, metricName, durationInNanos / 1.0E9f, transactionData.getGuid(), transactionData.getReferrerGuid(), port, transactionData.getTripId(), transactionData.getReferringPathHash(), transactionData.getAlternatePathHashes(), transactionData.getApdexPerfZone(), transactionData.getSyntheticsResourceId(), transactionData.getSyntheticsMonitorId(), transactionData.getSyntheticsJobId());
        if (transactionData.getTripId() != null) {
            event.pathHash = transactionData.generatePathHash();
        }
        event.queueDuration = this.retrieveMetricIfExists(transactionStats, "WebFrontend/QueueTime").getTotal();
        event.externalDuration = this.retrieveMetricIfExists(transactionStats, "External/all").getTotal();
        event.externalCallCount = this.retrieveMetricIfExists(transactionStats, "External/all").getCallCount();
        event.databaseDuration = this.retrieveMetricIfExists(transactionStats, "Datastore/all").getTotal();
        event.databaseCallCount = this.retrieveMetricIfExists(transactionStats, "Datastore/all").getCallCount();
        event.gcCumulative = this.retrieveMetricIfExists(transactionStats, "GC/cumulative").getTotal();
        if (ServiceFactory.getAttributesService().isAttributesEnabledForEvents(transactionData.getApplicationName())) {
            event.userAttributes = transactionData.getUserAttributes();
            (event.agentAttributes = transactionData.getAgentAttributes()).putAll(AttributesUtils.appendAttributePrefixes(transactionData.getPrefixedAttributes()));
        }
        return event;
    }
    
    private CountStats retrieveMetricIfExists(final TransactionStats transactionStats, final String metricName) {
        if (!transactionStats.getUnscopedStats().getStatsMap().containsKey(metricName)) {
            return NoCallCountStats.NO_STATS;
        }
        return transactionStats.getUnscopedStats().getResponseTimeStats(metricName);
    }
    
    public void configChanged(final String appName, final AgentConfig agentConfig) {
        this.isEnabledForApp.remove(appName);
    }
    
    public ReserviorSampledArrayList<TransactionEvent> unsafeGetEventData(final String appName) {
        return this.reservoirForApp.get(appName);
    }
    
    private static class NoCallCountStats extends AbstractStats
    {
        static final NoCallCountStats NO_STATS;
        
        public float getTotal() {
            return Float.NEGATIVE_INFINITY;
        }
        
        public float getTotalExclusiveTime() {
            return Float.NEGATIVE_INFINITY;
        }
        
        public float getMinCallTime() {
            return Float.NEGATIVE_INFINITY;
        }
        
        public float getMaxCallTime() {
            return Float.NEGATIVE_INFINITY;
        }
        
        public double getSumOfSquares() {
            return Double.NEGATIVE_INFINITY;
        }
        
        public boolean hasData() {
            return false;
        }
        
        public void reset() {
        }
        
        public void merge(final StatsBase stats) {
        }
        
        public Object clone() throws CloneNotSupportedException {
            return NoCallCountStats.NO_STATS;
        }
        
        static {
            NO_STATS = new NoCallCountStats();
        }
    }
}
