// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.reinstrument;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public class ReinstrumentResult
{
    protected static final String ERROR_KEY = "errors";
    protected static final String PCS_SPECIFIED_KEY = "pointcuts_specified";
    protected static final String RETRANSFORM_INIT_KEY = "retransform_init";
    private final List<String> errorMessages;
    private int pointCutsSpecified;
    private int pointCutsAdded;
    private Set<String> retranformedInitializedClasses;
    
    public ReinstrumentResult() {
        this.errorMessages = new ArrayList<String>();
        this.pointCutsSpecified = 0;
        this.pointCutsAdded = 0;
        this.retranformedInitializedClasses = new HashSet<String>();
    }
    
    public Map<String, Object> getStatusMap() {
        final Map<String, Object> statusMap = new HashMap<String, Object>();
        if (this.errorMessages.size() > 0) {
            final StringBuilder sb = new StringBuilder();
            final Iterator<String> it = this.errorMessages.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            statusMap.put("errors", sb.toString());
        }
        statusMap.put("pointcuts_specified", this.pointCutsSpecified);
        if (this.retranformedInitializedClasses.size() > 0) {
            final StringBuilder sb = new StringBuilder();
            final Iterator<String> it = this.retranformedInitializedClasses.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            statusMap.put("retransform_init", sb.toString());
        }
        return statusMap;
    }
    
    public void addErrorMessage(final String pErrorMessages) {
        this.errorMessages.add(pErrorMessages);
    }
    
    public void setPointCutsSpecified(final int pPointCutsSpecified) {
        this.pointCutsSpecified = pPointCutsSpecified;
    }
    
    public void setRetranformedInitializedClasses(final Set<String> pRetranformedInitializedClasses) {
        this.retranformedInitializedClasses = pRetranformedInitializedClasses;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("pointcuts_specified");
        sb.append(":");
        sb.append(this.pointCutsSpecified);
        sb.append(", ");
        if (this.errorMessages != null && this.errorMessages.size() > 0) {
            sb.append(",");
            sb.append("errors");
            sb.append(":[");
            for (final String msg : this.errorMessages) {
                sb.append(" ");
                sb.append(msg);
            }
            sb.append("]");
        }
        if (this.retranformedInitializedClasses != null && this.retranformedInitializedClasses.size() > 0) {
            sb.append(", ");
            sb.append("retransform_init");
            sb.append(":[");
            for (final String msg : this.retranformedInitializedClasses) {
                sb.append(" ");
                sb.append(msg);
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
