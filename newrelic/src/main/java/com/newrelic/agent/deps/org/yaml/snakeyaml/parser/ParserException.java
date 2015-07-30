// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.parser;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.MarkedYAMLException;

public class ParserException extends MarkedYAMLException
{
    private static final long serialVersionUID = -2349253802798398038L;
    
    public ParserException(final String context, final Mark contextMark, final String problem, final Mark problemMark) {
        super(context, contextMark, problem, problemMark, null);
    }
}
