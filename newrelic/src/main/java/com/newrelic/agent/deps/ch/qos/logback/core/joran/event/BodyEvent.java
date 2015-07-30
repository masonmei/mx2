// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.event;

import org.xml.sax.Locator;

public class BodyEvent extends SaxEvent
{
    private String text;
    
    BodyEvent(final String text, final Locator locator) {
        super(null, null, null, locator);
        this.text = text;
    }
    
    public String getText() {
        if (this.text != null) {
            return this.text.trim();
        }
        return this.text;
    }
    
    public String toString() {
        return "BodyEvent(" + this.getText() + ")" + this.locator.getLineNumber() + "," + this.locator.getColumnNumber();
    }
    
    public void append(final String str) {
        this.text += str;
    }
}
