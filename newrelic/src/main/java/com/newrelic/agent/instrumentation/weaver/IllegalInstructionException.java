// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.util.AgentError;

class IllegalInstructionException extends AgentError
{
    private static final long serialVersionUID = 4541357282999714780L;
    
    public IllegalInstructionException(final String message) {
        super(message);
    }
    
    public IllegalInstructionException(final String message, final Error ex) {
        super(message, ex);
    }
}
