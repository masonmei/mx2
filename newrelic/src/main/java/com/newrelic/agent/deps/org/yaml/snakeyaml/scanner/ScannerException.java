// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.scanner;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.MarkedYAMLException;

public class ScannerException extends MarkedYAMLException
{
    private static final long serialVersionUID = 4782293188600445954L;
    
    public ScannerException(final String context, final Mark contextMark, final String problem, final Mark problemMark, final String note) {
        super(context, contextMark, problem, problemMark, note);
    }
    
    public ScannerException(final String context, final Mark contextMark, final String problem, final Mark problemMark) {
        super(context, contextMark, problem, problemMark, null);
    }
}
