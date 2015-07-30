// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.boolex;

import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EvaluationException;
import java.util.Iterator;
import com.newrelic.agent.deps.org.slf4j.Marker;
import java.util.ArrayList;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EventEvaluatorBase;

public class OnMarkerEvaluator extends EventEvaluatorBase<ILoggingEvent>
{
    List<String> markerList;
    
    public OnMarkerEvaluator() {
        this.markerList = new ArrayList<String>();
    }
    
    public void addMarker(final String markerStr) {
        this.markerList.add(markerStr);
    }
    
    public boolean evaluate(final ILoggingEvent event) throws NullPointerException, EvaluationException {
        final Marker eventsMarker = event.getMarker();
        if (eventsMarker == null) {
            return false;
        }
        for (final String markerStr : this.markerList) {
            if (eventsMarker.contains(markerStr)) {
                return true;
            }
        }
        return false;
    }
}
