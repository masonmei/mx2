// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.reader;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;

public class ReaderException extends YAMLException
{
    private static final long serialVersionUID = 8710781187529689083L;
    private String name;
    private char character;
    private int position;
    
    public ReaderException(final String name, final int position, final char character, final String message) {
        super(message);
        this.name = name;
        this.character = character;
        this.position = position;
    }
    
    public String toString() {
        return "unacceptable character #" + Integer.toHexString(this.character).toUpperCase() + " " + this.getMessage() + "\nin \"" + this.name + "\", position " + this.position;
    }
}
