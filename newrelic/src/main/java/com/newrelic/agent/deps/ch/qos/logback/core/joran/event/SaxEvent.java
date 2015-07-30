// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.event;

import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.Locator;

public class SaxEvent
{
    public final String namespaceURI;
    public final String localName;
    public final String qName;
    public final Locator locator;
    
    SaxEvent(final String namespaceURI, final String localName, final String qName, final Locator locator) {
        this.namespaceURI = namespaceURI;
        this.localName = localName;
        this.qName = qName;
        this.locator = new LocatorImpl(locator);
    }
    
    public String getLocalName() {
        return this.localName;
    }
    
    public Locator getLocator() {
        return this.locator;
    }
    
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    public String getQName() {
        return this.qName;
    }
}
