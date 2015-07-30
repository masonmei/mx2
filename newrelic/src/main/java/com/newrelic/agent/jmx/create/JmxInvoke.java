// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.create;

import javax.management.MalformedObjectNameException;

public class JmxInvoke extends JmxObject
{
    private final String operationName;
    private final Object[] params;
    private final String[] signature;
    private int errorCount;
    
    public JmxInvoke(final String pObjectName, final String safeName, final String pOperationName, final Object[] pParams, final String[] pSignature) throws MalformedObjectNameException {
        super(pObjectName, safeName);
        this.errorCount = 0;
        this.operationName = pOperationName;
        this.params = pParams;
        this.signature = pSignature;
    }
    
    public String getOperationName() {
        return this.operationName;
    }
    
    public Object[] getParams() {
        return this.params;
    }
    
    public String[] getSignature() {
        return this.signature;
    }
    
    public int getErrorCount() {
        return this.errorCount;
    }
    
    public void incrementErrorCount() {
        ++this.errorCount;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("object_name: ").append(this.getObjectNameString());
        sb.append(" operation_name: ").append(this.operationName);
        sb.append(" error_count: ").append(this.errorCount);
        return sb.toString();
    }
}
