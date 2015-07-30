// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.Transaction;
import java.util.Map;

public class CustomAttributeSender extends AttributeSender
{
    protected static String ATTRIBUTE_TYPE;
    
    protected String getAttributeType() {
        return CustomAttributeSender.ATTRIBUTE_TYPE;
    }
    
    protected Map<String, Object> getAttributeMap() throws Throwable {
        final Transaction tx = Transaction.getTransaction();
        return tx.getUserAttributes();
    }
    
    public Object verifyParameterAndReturnValue(final String key, final Object value, final String methodCalled) {
        try {
            if (Transaction.getTransaction().getAgentConfig().isHighSecurity()) {
                Agent.LOG.log(Level.FINER, "Unable to add {0} attribute because {1} was invoked with key \"{2}\" while in high security mode.", new Object[] { this.getAttributeType(), methodCalled, key });
                return null;
            }
        }
        catch (Throwable t) {
            Agent.LOG.log(Level.FINEST, "Unable to verify attribute. Exception thrown while verifying high security mode.", t);
            return null;
        }
        return super.verifyParameterAndReturnValue(key, value, methodCalled);
    }
    
    protected void addCustomAttributeImpl(final String key, final Object value, final String methodName) {
        try {
            final Transaction tx = Transaction.getTransaction();
            if (this.getAttributeMap().size() >= tx.getAgentConfig().getMaxUserParameters()) {
                Agent.LOG.log(Level.FINER, "Unable to add {0} attribute for key \"{1}\" because the limit is {2}.", new Object[] { this.getAttributeType(), key, tx.getAgentConfig().getMaxUserParameters() });
                return;
            }
            super.addCustomAttributeImpl(key, value, methodName);
        }
        catch (Throwable t) {
            Agent.LOG.log(Level.FINER, "Exception adding {0} parameter for key: \"{1}\": {2}", new Object[] { this.getAttributeType(), key, t });
        }
    }
    
    static {
        CustomAttributeSender.ATTRIBUTE_TYPE = "custom";
    }
}
