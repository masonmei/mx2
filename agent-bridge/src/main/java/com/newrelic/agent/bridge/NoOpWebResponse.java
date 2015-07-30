// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

public class NoOpWebResponse implements WebResponse
{
    public static final WebResponse INSTANCE;
    
    public void setStatus(final int statusCode) {
    }
    
    public int getStatus() {
        return 0;
    }
    
    public void setStatusMessage(final String message) {
    }
    
    public String getStatusMessage() {
        return "";
    }
    
    public void freezeStatus() {
    }
    
    static {
        INSTANCE = new NoOpWebResponse();
    }
}
