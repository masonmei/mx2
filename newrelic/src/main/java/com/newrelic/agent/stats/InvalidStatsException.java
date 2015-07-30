// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

public class InvalidStatsException extends RuntimeException
{
    private static final long serialVersionUID = -4680720624395039293L;
    
    InvalidStatsException(final String message) {
        super(message);
    }
}
