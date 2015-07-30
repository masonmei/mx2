// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transport;

public class ReadResult
{
    private final int responseCode;
    private final String responseBody;
    
    protected ReadResult(final int responseCode, final String responseBody) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }
    
    protected int getResponseCode() {
        return this.responseCode;
    }
    
    protected String getResponseBody() {
        return this.responseBody;
    }
    
    public static ReadResult create(final int responseCode, final String responseBody) {
        return new ReadResult(responseCode, responseBody);
    }
}
