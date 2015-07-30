// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.create;

import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class JmxObject
{
    private final String objectNameString;
    private final ObjectName objectName;
    
    public JmxObject(final String pObjectName, final String safeName) throws MalformedObjectNameException {
        this.objectNameString = pObjectName;
        this.objectName = this.setObjectName(safeName);
    }
    
    private ObjectName setObjectName(String safeName) throws MalformedObjectNameException {
        try {
            return new ObjectName(safeName);
        }
        catch (MalformedObjectNameException e) {
            if (!this.objectNameString.equals(safeName)) {
                safeName = safeName + '(' + this.objectNameString + ')';
            }
            if (Agent.LOG.isFineEnabled()) {
                Agent.LOG.severe(MessageFormat.format("Skipping bad Jmx object name : {0}.  {1}", safeName, e.toString()));
                Agent.LOG.log(Level.FINER, "Jmx config error", e);
            }
            throw e;
        }
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("object_name: ").append(this.objectNameString);
        return sb.toString();
    }
    
    public String getObjectNameString() {
        return this.objectNameString;
    }
    
    public ObjectName getObjectName() {
        return this.objectName;
    }
}
