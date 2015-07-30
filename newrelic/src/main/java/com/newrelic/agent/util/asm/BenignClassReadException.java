// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import java.io.IOException;

public class BenignClassReadException extends IOException
{
    private static final long serialVersionUID = -6999790175012592488L;
    
    public BenignClassReadException(final String message) {
        super(message);
    }
}
