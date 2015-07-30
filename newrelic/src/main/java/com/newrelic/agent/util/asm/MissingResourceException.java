// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import java.io.IOException;

public class MissingResourceException extends IOException
{
    private static final long serialVersionUID = 1177827391206078775L;
    
    public MissingResourceException(final String message) {
        super(message);
    }
}
