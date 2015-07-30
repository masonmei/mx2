// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.constructor;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.MarkedYAMLException;

public class ConstructorException extends MarkedYAMLException
{
    private static final long serialVersionUID = -8816339931365239910L;
    
    protected ConstructorException(final String context, final Mark contextMark, final String problem, final Mark problemMark) {
        super(context, contextMark, problem, problemMark);
    }
}
