// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.composer;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.MarkedYAMLException;

public class ComposerException extends MarkedYAMLException
{
    private static final long serialVersionUID = 2146314636913113935L;
    
    protected ComposerException(final String context, final Mark contextMark, final String problem, final Mark problemMark) {
        super(context, contextMark, problem, problemMark);
    }
}
