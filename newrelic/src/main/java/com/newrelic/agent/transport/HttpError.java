// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transport;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import java.text.MessageFormat;
import java.util.Map;

public class HttpError extends Exception
{
    private static final long serialVersionUID = 1L;
    private static final Map<Integer, String> RESPONSE_MESSAGES;
    private final int statusCode;
    
    public HttpError(final String message, final int statusCode) {
        super((message == null) ? Integer.toString(statusCode) : message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public static HttpError create(final int statusCode, final String host) {
        String messageFormat = HttpError.RESPONSE_MESSAGES.get(statusCode);
        if (messageFormat == null) {
            messageFormat = "Received a {1} response from {0}";
        }
        final String message = MessageFormat.format(messageFormat, host, statusCode);
        return new HttpError(message, statusCode);
    }
    
    public boolean isRetryableError() {
        return this.statusCode != 413 && this.statusCode != 415;
    }
    
    static {
        RESPONSE_MESSAGES = ImmutableMap.of(413, "The data post was too large ({1})", 415, "An error occurred serializing data ({1})", 500, "{0} encountered an internal error ({1})", 503, "{0} is temporarily unavailable ({1})");
    }
}
