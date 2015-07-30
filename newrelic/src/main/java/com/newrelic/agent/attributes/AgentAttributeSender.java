// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import com.newrelic.agent.Transaction;
import java.util.Map;

public class AgentAttributeSender extends AttributeSender
{
    protected static String ATTRIBUTE_TYPE;
    
    protected String getAttributeType() {
        return AgentAttributeSender.ATTRIBUTE_TYPE;
    }
    
    protected Map<String, Object> getAttributeMap() throws Throwable {
        final Transaction tx = Transaction.getTransaction();
        return tx.getAgentAttributes();
    }
    
    static {
        AgentAttributeSender.ATTRIBUTE_TYPE = "agent";
    }
}
