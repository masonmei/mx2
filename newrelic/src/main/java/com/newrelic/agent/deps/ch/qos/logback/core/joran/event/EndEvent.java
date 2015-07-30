// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.event;

import org.xml.sax.Locator;

public class EndEvent extends SaxEvent
{
    EndEvent(final String namespaceURI, final String localName, final String qName, final Locator locator) {
        super(namespaceURI, localName, qName, locator);
    }
    
    public String toString() {
        return "  EndEvent(" + this.getQName() + ")  [" + this.locator.getLineNumber() + "," + this.locator.getColumnNumber() + "]";
    }
}
