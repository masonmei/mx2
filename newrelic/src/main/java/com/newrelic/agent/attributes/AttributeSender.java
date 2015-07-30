// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import com.newrelic.agent.Transaction;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Map;

public abstract class AttributeSender
{
    protected static String ATTRIBUTE_TYPE;
    
    protected abstract String getAttributeType();
    
    protected abstract Map<String, Object> getAttributeMap() throws Throwable;
    
    protected void addCustomAttributeImpl(final String key, final Object value, final String methodName) {
        final Object filteredValue = this.verifyParameterAndReturnValue(key, value, methodName);
        if (null == filteredValue) {
            return;
        }
        try {
            this.getAttributeMap().put(key, filteredValue);
            Agent.LOG.log(Level.FINER, "Added {0} attribute \"{1}\": {2}", new Object[] { this.getAttributeType(), key, filteredValue });
        }
        catch (Throwable t) {
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.FINEST, "Exception adding attribute for key: \"{0}\": {1}", new Object[] { key, t });
            }
            else if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, "Exception adding attribute for key: \"{0}\": {1}", new Object[] { key });
            }
        }
    }
    
    public Object verifyParameterAndReturnValue(final String key, final Object value, final String methodCalled) {
        if (key == null) {
            Agent.LOG.log(Level.FINER, "Unable to add {0} attribute because {1} was invoked with a null key", new Object[] { this.getAttributeType(), methodCalled });
            return null;
        }
        if (value == null) {
            Agent.LOG.log(Level.FINER, "Unable to add {0} attribute because {1} was invoked with a null value for key \"{2}\"", new Object[] { this.getAttributeType(), methodCalled, key });
            return null;
        }
        try {
            final Transaction tx = Transaction.getTransaction();
            if (tx == null || !tx.isInProgress()) {
                Agent.LOG.log(Level.FINER, "Unable to add {0} attribute with key \"{1}\" because {2} was invoked outside a New Relic transaction.", new Object[] { this.getAttributeType(), key, methodCalled });
                return null;
            }
            if (key.length() > tx.getAgentConfig().getMaxUserParameterSize()) {
                Agent.LOG.log(Level.FINER, "Unable to add {0} attribute because {1} was invoked with a key longer than {2} bytes. Key is \"{3}\".", new Object[] { this.getAttributeType(), methodCalled, tx.getAgentConfig().getMaxUserParameterSize(), key });
                return null;
            }
            if (value instanceof String && ((String)value).length() > tx.getAgentConfig().getMaxUserParameterSize()) {
                Agent.LOG.log(Level.FINER, "{0} was invoked with a value longer than {2} bytes for key \"{3}\". The value will be shortened.", new Object[] { methodCalled, value, tx.getAgentConfig().getMaxUserParameterSize(), key });
                return ((String)value).substring(0, tx.getAgentConfig().getMaxUserParameterSize());
            }
        }
        catch (Throwable t) {
            Agent.LOG.log(Level.FINEST, "Exception while verifying attribute", t);
            return null;
        }
        return value;
    }
    
    public void addAttribute(final String key, final String value, final String methodName) {
        this.addCustomAttributeImpl(key, value, methodName);
    }
    
    public void addAttribute(final String key, final Number value, final String methodName) {
        this.addCustomAttributeImpl(key, value, methodName);
    }
}
