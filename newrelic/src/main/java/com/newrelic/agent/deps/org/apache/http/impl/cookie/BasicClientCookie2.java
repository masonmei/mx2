// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import java.util.Date;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import java.io.Serializable;
import com.newrelic.agent.deps.org.apache.http.cookie.SetCookie2;

@NotThreadSafe
public class BasicClientCookie2 extends BasicClientCookie implements SetCookie2, Serializable
{
    private static final long serialVersionUID = -7744598295706617057L;
    private String commentURL;
    private int[] ports;
    private boolean discard;
    
    public BasicClientCookie2(final String name, final String value) {
        super(name, value);
    }
    
    public int[] getPorts() {
        return this.ports;
    }
    
    public void setPorts(final int[] ports) {
        this.ports = ports;
    }
    
    public String getCommentURL() {
        return this.commentURL;
    }
    
    public void setCommentURL(final String commentURL) {
        this.commentURL = commentURL;
    }
    
    public void setDiscard(final boolean discard) {
        this.discard = discard;
    }
    
    public boolean isPersistent() {
        return !this.discard && super.isPersistent();
    }
    
    public boolean isExpired(final Date date) {
        return this.discard || super.isExpired(date);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final BasicClientCookie2 clone = (BasicClientCookie2)super.clone();
        if (this.ports != null) {
            clone.ports = this.ports.clone();
        }
        return clone;
    }
}
