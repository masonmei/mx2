// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

import java.util.Iterator;
import java.util.List;

public class MissingOptionException extends ParseException
{
    private List missingOptions;
    
    public MissingOptionException(final String message) {
        super(message);
    }
    
    public MissingOptionException(final List missingOptions) {
        this(createMessage(missingOptions));
        this.missingOptions = missingOptions;
    }
    
    public List getMissingOptions() {
        return this.missingOptions;
    }
    
    private static String createMessage(final List missingOptions) {
        final StringBuffer buff = new StringBuffer("Missing required option");
        buff.append((missingOptions.size() == 1) ? "" : "s");
        buff.append(": ");
        final Iterator it = missingOptions.iterator();
        while (it.hasNext()) {
            buff.append(it.next());
            if (it.hasNext()) {
                buff.append(", ");
            }
        }
        return buff.toString();
    }
}
