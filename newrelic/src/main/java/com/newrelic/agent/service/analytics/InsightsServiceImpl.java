// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.analytics;

import java.util.concurrent.LinkedBlockingQueue;
import com.newrelic.agent.deps.com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.com.google.common.cache.CacheBuilder;
import com.newrelic.api.agent.Insights;
import java.util.concurrent.ExecutionException;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.List;
import java.util.Collections;
import java.text.MessageFormat;
import java.util.Iterator;
import com.newrelic.agent.Transaction;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Map;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.config.AgentConfig;
import java.util.Collection;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.TransactionData;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.TransactionListener;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.deps.com.google.common.cache.LoadingCache;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.newrelic.agent.service.AbstractService;

public class InsightsServiceImpl extends AbstractService implements InsightsService
{
    private final boolean enabled;
    private final ConcurrentMap<String, Boolean> isEnabledForApp;
    private final int maxSamplesStored;
    private final ConcurrentHashMap<String, ReserviorSampledArrayList<CustomInsightsEvent>> reservoirForApp;
    private static final LoadingCache<String, String> stringCache;
    protected final HarvestListener harvestListener;
    protected final TransactionListener transactionListener;
    protected final AgentConfigListener configListener;
    
    public InsightsServiceImpl() {
        super("Insights");
        this.isEnabledForApp = new ConcurrentHashMap<String, Boolean>();
        this.reservoirForApp = new ConcurrentHashMap<String, ReserviorSampledArrayList<CustomInsightsEvent>>();
        this.harvestListener = new HarvestListener() {
            public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
                InsightsServiceImpl.this.harvest(appName, statsEngine);
            }
            
            public void afterHarvest(final String appName) {
            }
        };
        this.transactionListener = new TransactionListener() {
            public void dispatcherTransactionFinished(final TransactionData transactionData, final TransactionStats transactionStats) {
                final TransactionInsights data = (TransactionInsights)transactionData.getInsightsData();
                InsightsServiceImpl.this.storeEvents(transactionData.getApplicationName(), data.events);
            }
        };
        this.configListener = new AgentConfigListener() {
            public void configChanged(final String appName, final AgentConfig agentConfig) {
                InsightsServiceImpl.this.isEnabledForApp.remove(appName);
            }
        };
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.maxSamplesStored = CustomInsightsEventsConfigUtils.getMaxSamplesStored(config);
        this.enabled = CustomInsightsEventsConfigUtils.isCustomInsightsEventsEnabled(config, this.maxSamplesStored);
        this.isEnabledForApp.put(config.getApplicationName(), this.enabled);
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    protected void doStart() throws Exception {
        ServiceFactory.getHarvestService().addHarvestListener(this.harvestListener);
        ServiceFactory.getTransactionService().addTransactionListener(this.transactionListener);
        ServiceFactory.getConfigService().addIAgentConfigListener(this.configListener);
    }
    
    protected void doStop() throws Exception {
        ServiceFactory.getHarvestService().removeHarvestListener(this.harvestListener);
        ServiceFactory.getTransactionService().removeTransactionListener(this.transactionListener);
        ServiceFactory.getConfigService().removeIAgentConfigListener(this.configListener);
        this.reservoirForApp.clear();
        this.isEnabledForApp.clear();
        InsightsServiceImpl.stringCache.invalidateAll();
    }
    
    public void recordCustomEvent(final String eventType, final Map<String, Object> attributes) {
        if (!this.enabled) {
            if (ServiceFactory.getConfigService().getDefaultAgentConfig().isHighSecurity()) {
                Agent.LOG.log(Level.FINER, "Event of type {0} not collected due to high security mode being enabled.", new Object[] { eventType });
            }
            else {
                Agent.LOG.log(Level.FINER, "Event of type {0} not collected. custom_insights_events not enabled.", new Object[] { eventType });
            }
            return;
        }
        if (AnalyticsEvent.isValidType(eventType)) {
            final Transaction transaction = ServiceFactory.getTransactionService().getTransaction(false);
            if (transaction == null || !transaction.isInProgress()) {
                final String applicationName = ServiceFactory.getRPMService().getApplicationName();
                final AgentConfig agentConfig = ServiceFactory.getConfigService().getAgentConfig(applicationName);
                if (!this.getIsEnabledForApp(agentConfig, applicationName)) {
                    this.reservoirForApp.remove(applicationName);
                    return;
                }
                this.storeEvent(applicationName, eventType, attributes);
            }
            else {
                transaction.getInsightsData().recordCustomEvent(eventType, (Map)attributes);
            }
        }
        else {
            Agent.LOG.log(Level.WARNING, "Custom event with invalid type of {0} was reported but ignored. Event types must match /^[a-zA-Z0-9:_ ]+$/ and be less than 256 chars.", new Object[] { eventType });
        }
    }
    
    private void storeEvents(final String appName, final Collection<CustomInsightsEvent> events) {
        if (events.size() > 0) {
            final ReserviorSampledArrayList<CustomInsightsEvent> eventList = this.getReservoir(appName);
            for (final CustomInsightsEvent event : events) {
                final Integer slot = eventList.getSlot();
                if (slot != null) {
                    eventList.set(slot, event);
                }
            }
        }
    }
    
    public void storeEvent(final String appName, final CustomInsightsEvent event) {
        final ReserviorSampledArrayList<CustomInsightsEvent> eventList = this.getReservoir(appName);
        final Integer slot = eventList.getSlot();
        if (slot != null) {
            eventList.set(slot, event);
            Agent.LOG.finest(MessageFormat.format("Added Custom Event of type {0}", event.type));
        }
    }
    
    private void storeEvent(final String appName, final String eventType, final Map<String, Object> attributes) {
        final ReserviorSampledArrayList<CustomInsightsEvent> eventList = this.getReservoir(appName);
        final Integer slot = eventList.getSlot();
        if (slot != null) {
            eventList.set(slot, new CustomInsightsEvent(mapInternString(eventType), System.currentTimeMillis(), copyAndInternStrings(attributes)));
            Agent.LOG.finest(MessageFormat.format("Added Custom Event of type {0}", eventType));
        }
    }
    
    private ReserviorSampledArrayList<CustomInsightsEvent> getReservoir(final String appName) {
        ReserviorSampledArrayList<CustomInsightsEvent> result;
        for (result = this.reservoirForApp.get(appName); result == null; result = this.reservoirForApp.get(appName)) {
            this.reservoirForApp.putIfAbsent(appName, new ReserviorSampledArrayList<CustomInsightsEvent>(this.maxSamplesStored));
        }
        return result;
    }
    
    void harvest(final String appName, final StatsEngine statsEngine) {
        if (!this.getIsEnabledForApp(ServiceFactory.getConfigService().getAgentConfig(appName), appName)) {
            this.reservoirForApp.remove(appName);
            return;
        }
        final ReserviorSampledArrayList<CustomInsightsEvent> reservoir = this.reservoirForApp.put(appName, new ReserviorSampledArrayList<CustomInsightsEvent>(this.maxSamplesStored));
        if (reservoir != null && reservoir.size() > 0) {
            try {
                ServiceFactory.getRPMService(appName).sendCustomAnalyticsEvents(Collections.unmodifiableList(reservoir));
                statsEngine.getStats("Supportability/Events/Customer/Sent").incrementCallCount(reservoir.size());
                statsEngine.getStats("Supportability/Events/Customer/Seen").incrementCallCount(reservoir.getNumberOfTries());
                if (reservoir.size() < reservoir.getNumberOfTries()) {
                    Agent.LOG.log(Level.WARNING, "Dropped {0} custom events out of {1}.", new Object[] { reservoir.getNumberOfTries() - reservoir.size(), reservoir.getNumberOfTries() });
                }
            }
            catch (Exception e) {
                Agent.LOG.fine("Unable to send custom events. Unsent events will be included in the next harvest.");
                final ReserviorSampledArrayList<CustomInsightsEvent> currentReservoir = this.reservoirForApp.get(appName);
                currentReservoir.addAll(reservoir);
            }
        }
    }
    
    private boolean getIsEnabledForApp(final AgentConfig config, final String currentAppName) {
        Boolean appEnabled = (currentAppName == null) ? null : this.isEnabledForApp.get(currentAppName);
        if (appEnabled == null) {
            appEnabled = CustomInsightsEventsConfigUtils.isCustomInsightsEventsEnabled(config, CustomInsightsEventsConfigUtils.getMaxSamplesStored(config));
            this.isEnabledForApp.put(currentAppName, appEnabled);
        }
        return appEnabled;
    }
    
    private static Map<String, Object> copyAndInternStrings(final Map<String, Object> attributes) {
        final Map<String, Object> result = Maps.newHashMap();
        for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (entry.getValue() instanceof String) {
                result.put(mapInternString(entry.getKey()), mapInternString((String) entry.getValue()));
            }
            else {
                result.put(mapInternString(entry.getKey()), entry.getValue());
            }
        }
        return result;
    }
    
    private static String mapInternString(final String value) {
        try {
            return InsightsServiceImpl.stringCache.get(value);
        }
        catch (ExecutionException e) {
            return value;
        }
    }
    
    public Insights getTransactionInsights(final AgentConfig config) {
        return (Insights)new TransactionInsights(config);
    }
    
    static {
        stringCache = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterAccess(70L, TimeUnit.SECONDS).build((CacheLoader<? super String, String>)new CacheLoader<String, String>() {
            public String load(final String key) throws Exception {
                return key;
            }
        });
    }
    
    static final class TransactionInsights implements Insights
    {
        final LinkedBlockingQueue<CustomInsightsEvent> events;
        
        TransactionInsights(final AgentConfig config) {
            final int maxSamplesStored = CustomInsightsEventsConfigUtils.getMaxSamplesStored(config);
            this.events = new LinkedBlockingQueue<CustomInsightsEvent>(maxSamplesStored);
        }
        
        public void recordCustomEvent(final String eventType, final Map<String, Object> attributes) {
            if (ServiceFactory.getConfigService().getDefaultAgentConfig().isHighSecurity()) {
                Agent.LOG.log(Level.FINER, "Event of type {0} not collected due to high security mode being enabled.", new Object[] { eventType });
                return;
            }
            if (AnalyticsEvent.isValidType(eventType)) {
                final CustomInsightsEvent event = new CustomInsightsEvent(mapInternString(eventType), System.currentTimeMillis(), copyAndInternStrings(attributes));
                if (this.events.offer(event)) {
                    Agent.LOG.finest(MessageFormat.format("Added Custom Event of type {0} in Transaction.", eventType));
                }
                else {
                    final String applicationName = ServiceFactory.getRPMService().getApplicationName();
                    ServiceFactory.getServiceManager().getInsights().storeEvent(applicationName, event);
                }
            }
            else {
                Agent.LOG.log(Level.WARNING, "Custom event with invalid type of {0} was reported for a transaction but ignored. Event types must match /^[a-zA-Z0-9:_ ]+$/ and be less than 256 chars.", new Object[] { eventType });
            }
        }
    }
}
