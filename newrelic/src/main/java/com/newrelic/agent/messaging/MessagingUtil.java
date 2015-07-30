// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.messaging;

import java.util.Iterator;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.LinkedHashMap;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Map;
import com.newrelic.agent.Transaction;

public final class MessagingUtil
{
    public static void recordParameters(final Transaction tx, final Map<String, String> requestParameters) {
        if (tx.isIgnore()) {
            return;
        }
        if (!ServiceFactory.getAttributesService().captureMessageParams(tx.getApplicationName())) {
            return;
        }
        if (requestParameters.isEmpty()) {
            return;
        }
        tx.getPrefixedAgentAttributes().put("message.parameters.", filterMessageParameters(requestParameters, tx.getAgentConfig().getMaxUserParameterSize()));
    }
    
    static Map<String, String> filterMessageParameters(final Map<String, String> messageParams, final int maxSizeLimit) {
        final Map<String, String> atts = new LinkedHashMap<String, String>();
        for (final Map.Entry<String, String> current : messageParams.entrySet()) {
            if (current.getKey().length() > maxSizeLimit) {
                Agent.LOG.log(Level.FINER, "Rejecting request parameter with key \"{0}\" because the key is over the size limit of {1}", new Object[] { current.getKey(), maxSizeLimit });
            }
            else {
                final String value = getValue(current.getValue(), maxSizeLimit);
                if (value == null) {
                    continue;
                }
                atts.put(current.getKey(), value);
            }
        }
        return atts;
    }
    
    private static String getValue(String value, final int maxSizeLimit) {
        if (value == null || value.length() == 0) {
            return null;
        }
        if (value.length() > maxSizeLimit) {
            value = value.substring(0, maxSizeLimit);
        }
        return value;
    }
}
