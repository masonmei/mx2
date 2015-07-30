// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.command;

import java.text.MessageFormat;
import java.io.File;

public class XmlInstrumentParams
{
    private File filePath;
    private boolean debug;
    
    public XmlInstrumentParams() {
        this.debug = false;
    }
    
    public void setFile(final String[] pFile, final String tagName) {
        final String fileName = this.verifyOne(pFile, tagName);
        this.filePath = new File(fileName);
        if (!this.filePath.exists()) {
            throw new IllegalArgumentException(MessageFormat.format("The file specified with the tag {0} does not exist.", tagName));
        }
        if (!this.filePath.isFile()) {
            throw new IllegalArgumentException(MessageFormat.format("The file specified with the tag {0} must be a file and is not.", tagName));
        }
        if (!this.filePath.canRead()) {
            throw new IllegalArgumentException(MessageFormat.format("The file specified with the tag {0} must be readable and is not.", tagName));
        }
    }
    
    public File getFile() {
        return this.filePath;
    }
    
    public void setDebug(final String[] pDebug, final String tagName) {
        final String value = this.verifyOneOrNone(pDebug, tagName);
        if (value != null) {
            this.debug = Boolean.parseBoolean(value);
        }
    }
    
    public boolean isDebug() {
        return this.debug;
    }
    
    private String verifyOne(final String[] value, final String tagName) {
        String toReturn = null;
        if (value == null || value.length != 1) {
            throw new IllegalArgumentException(MessageFormat.format("One {0}, and only one {0}, must be set.", tagName));
        }
        toReturn = value[0];
        if (toReturn != null) {
            toReturn = toReturn.trim();
            return toReturn;
        }
        throw new IllegalArgumentException(MessageFormat.format("One {0}, and only one {0}, must be set.", tagName));
    }
    
    private String verifyOneOrNone(final String[] value, final String tagName) {
        String toReturn = null;
        if (value == null) {
            return null;
        }
        if (value.length != 1) {
            throw new IllegalArgumentException(MessageFormat.format("One {0}, and only one {0}, must be set.", tagName));
        }
        toReturn = value[0];
        if (toReturn == null) {
            return null;
        }
        toReturn = toReturn.trim();
        return toReturn;
    }
}
