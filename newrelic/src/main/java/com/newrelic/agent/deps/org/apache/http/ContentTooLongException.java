// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.io.IOException;

public class ContentTooLongException extends IOException
{
    private static final long serialVersionUID = -924287689552495383L;
    
    public ContentTooLongException(final String message) {
        super(message);
    }
}
