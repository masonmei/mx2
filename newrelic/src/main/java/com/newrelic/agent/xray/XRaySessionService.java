// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.xray;

import java.util.Collections;
import java.util.Collection;
import com.newrelic.agent.IRPMService;
import java.util.Set;
import java.util.HashSet;
import com.newrelic.agent.stats.StatsEngine;
import java.util.Iterator;
import com.newrelic.agent.Agent;
import com.newrelic.agent.commands.Command;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.AbstractService;

public class XRaySessionService extends AbstractService implements HarvestListener, IXRaySessionService
{
    private final Map<Long, XRaySession> sessions;
    private final boolean enabled;
    private final List<XRaySessionListener> listeners;
    public static final int MAX_SESSION_COUNT = 50;
    public static final long MAX_SESSION_DURATION_SECONDS = 86400L;
    public static final long MAX_TRACE_COUNT = 100L;
    
    public XRaySessionService() {
        super(XRaySessionService.class.getSimpleName());
        this.sessions = new HashMap<Long, XRaySession>();
        this.listeners = new CopyOnWriteArrayList<XRaySessionListener>();
        this.enabled = ServiceFactory.getConfigService().getDefaultAgentConfig().isXraySessionEnabled();
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    protected void doStart() throws Exception {
        this.addCommands();
        ServiceFactory.getHarvestService().addHarvestListener(this);
    }
    
    protected void doStop() throws Exception {
        ServiceFactory.getHarvestService().removeHarvestListener(this);
    }
    
    private void addCommands() {
        ServiceFactory.getCommandParser().addCommands(new StartXRayCommand(this));
    }
    
    private void addSession(final XRaySession newSession) {
        if (this.listeners.size() < 50) {
            Agent.LOG.info("Adding X-Ray session: " + newSession.toString());
            this.sessions.put(newSession.getxRayId(), newSession);
            for (final XRaySessionListener listener : this.listeners) {
                listener.xraySessionCreated(newSession);
            }
        }
        else {
            Agent.LOG.error("Unable to add X-Ray Session because this would exceed the maximum number of concurrent X-Ray Sessions allowed.  Max allowed is 50");
        }
    }
    
    private void removeSession(final Long sessionId) {
        final XRaySession session = this.sessions.remove(sessionId);
        if (null == session) {
            Agent.LOG.info("Tried to remove X-Ray session " + sessionId + " but no such session exists.");
        }
        else {
            Agent.LOG.info("Removing X-Ray session: " + session.toString());
            for (final XRaySessionListener listener : this.listeners) {
                listener.xraySessionRemoved(session);
            }
        }
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
    }
    
    public void afterHarvest(final String appName) {
        final Set<Long> expired = new HashSet<Long>();
        for (final XRaySession session : this.sessions.values()) {
            if (session.sessionHasExpired()) {
                expired.add(session.getxRayId());
                Agent.LOG.debug("Identified X-Ray session for expiration: " + session.toString());
            }
        }
        for (final Long key : expired) {
            final XRaySession session2 = this.sessions.get(key);
            if (null != session2) {
                Agent.LOG.info("Expiring X-Ray session: " + session2.getxRaySessionName());
                this.removeSession(key);
            }
        }
    }
    
    void setupSession(final Map<?, ?> sessionMap, final String applicationName) {
        Long xRayId = null;
        Boolean runProfiler = null;
        String keyTransactionName = null;
        Double samplePeriod = null;
        String xRaySessionName = null;
        Long duration = null;
        Long requestedTraceCount = null;
        final Object x_ray_id = sessionMap.remove("x_ray_id");
        if (x_ray_id instanceof Long) {
            xRayId = (Long)x_ray_id;
        }
        final Object run_profiler = sessionMap.remove("run_profiler");
        if (run_profiler instanceof Boolean) {
            runProfiler = (Boolean)run_profiler;
        }
        final Object key_transaction_name = sessionMap.remove("key_transaction_name");
        if (key_transaction_name instanceof String) {
            keyTransactionName = (String)key_transaction_name;
        }
        final Object sample_period = sessionMap.remove("sample_period");
        if (sample_period instanceof Double) {
            samplePeriod = (Double)sample_period;
        }
        final Object xray_session_name = sessionMap.remove("xray_session_name");
        if (xray_session_name instanceof String) {
            xRaySessionName = (String)xray_session_name;
        }
        final Object duration_obj = sessionMap.remove("duration");
        if (duration_obj instanceof Long) {
            duration = (Long)duration_obj;
            if (duration < 0L) {
                duration = 0L;
                Agent.LOG.error("Tried to create an X-Ray Session with negative duration, setting duration to 0");
            }
            else if (duration > 86400L) {
                Agent.LOG.error("Tried to create an X-Ray session with a duration (" + duration + ") longer than " + 86400L + " seconds.  Setting the duration to " + 86400L + " seconds");
                duration = 86400L;
            }
        }
        final Object requested_trace_count = sessionMap.remove("requested_trace_count");
        if (requested_trace_count instanceof Long) {
            requestedTraceCount = (Long)requested_trace_count;
            if (requestedTraceCount > 100L) {
                Agent.LOG.error("Tried to create an X-Ray session with a requested trace count (" + requestedTraceCount + ") larger than " + 100L + ".  Setting the max trace count to " + 100L);
                requestedTraceCount = 100L;
            }
            else if (requestedTraceCount < 0L) {
                Agent.LOG.error("Tried to create an X-Ray Session with negative trace count, setting trace count to 0");
                requestedTraceCount = 0L;
            }
        }
        final XRaySession newSession = new XRaySession(xRayId, runProfiler, keyTransactionName, samplePeriod, xRaySessionName, duration, requestedTraceCount, applicationName);
        this.addSession(newSession);
    }
    
    public Map<?, ?> processSessionsList(final List<Long> incomingList, final IRPMService rpmService) {
        final Set<Long> sessionIdsToAdd = new HashSet<Long>();
        final Set<Long> sessionIdsToRemove = new HashSet<Long>();
        final String applicationName = rpmService.getApplicationName();
        for (final Long id : incomingList) {
            if (!this.sessions.keySet().contains(id)) {
                sessionIdsToAdd.add(id);
            }
        }
        for (final long id2 : this.sessions.keySet()) {
            if (!incomingList.contains(id2)) {
                Agent.LOG.debug("Identified " + id2 + " for removal from the active list of X-Ray sessions");
                sessionIdsToRemove.add(id2);
            }
        }
        if (sessionIdsToRemove.size() > 0) {
            Agent.LOG.debug("Removing " + sessionIdsToRemove + " from the active list of X-Ray sessions");
            for (final Long id : sessionIdsToRemove) {
                this.removeSession(id);
            }
        }
        if (sessionIdsToAdd.size() > 0) {
            Agent.LOG.debug("Fetching details for " + sessionIdsToAdd + " to add to the active list of X Ray Sessions");
            Collection<?> newSessionDetails;
            try {
                newSessionDetails = rpmService.getXRaySessionInfo(sessionIdsToAdd);
            }
            catch (Exception e) {
                Agent.LOG.error("Unable to fetch X-Ray session details from RPM" + e.getMessage());
                return (Map<?, ?>)Collections.EMPTY_MAP;
            }
            for (final Object newSession : newSessionDetails) {
                if (newSession instanceof Map) {
                    final Map<?, ?> newSessionMap = (Map<?, ?>)newSession;
                    this.setupSession(newSessionMap, applicationName);
                }
                else {
                    Agent.LOG.error("Unable to read X-Ray session details: " + newSession);
                }
            }
        }
        Agent.LOG.debug("Resulting collection of X-Ray sessions: " + this.sessions);
        return (Map<?, ?>)Collections.EMPTY_MAP;
    }
    
    public void addListener(final XRaySessionListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeListener(final XRaySessionListener listener) {
        this.listeners.remove(listener);
    }
}
