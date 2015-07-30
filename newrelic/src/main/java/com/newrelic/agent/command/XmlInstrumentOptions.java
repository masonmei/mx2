// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.command;

public enum XmlInstrumentOptions
{
    FILE_PATH("file", true, "The full path to the xml extension file to be validated. This must be set.") {
        public void validateAndAddParameter(final XmlInstrumentParams pInstrument, final String[] pValue, final String pTagName) {
            pInstrument.setFile(pValue, pTagName);
        }
    }, 
    DEBUG_FLAG("debug", true, "Set this flag to true for more debuging information. The default is false.") {
        public void validateAndAddParameter(final XmlInstrumentParams pInstrument, final String[] pValue, final String pTagName) {
            pInstrument.setDebug(pValue, pTagName);
        }
    };
    
    private final String flagName;
    private boolean argRequired;
    private final String description;
    
    private XmlInstrumentOptions(final String pFlagName, final boolean pRequired, final String pDescription) {
        this.flagName = pFlagName;
        this.argRequired = pRequired;
        this.description = pDescription;
    }
    
    public abstract void validateAndAddParameter(final XmlInstrumentParams p0, final String[] p1, final String p2);
    
    public String getFlagName() {
        return this.flagName;
    }
    
    public boolean isArgRequired() {
        return this.argRequired;
    }
    
    public String getDescription() {
        return this.description;
    }
}
