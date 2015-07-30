// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.event;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Locator;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Pattern;
import org.xml.sax.Attributes;

public class StartEvent extends SaxEvent
{
    public final Attributes attributes;
    public final Pattern pattern;
    
    StartEvent(final Pattern pattern, final String namespaceURI, final String localName, final String qName, final Attributes attributes, final Locator locator) {
        super(namespaceURI, localName, qName, locator);
        this.attributes = new AttributesImpl(attributes);
        this.pattern = pattern;
    }
    
    public Attributes getAttributes() {
        return this.attributes;
    }
    
    public String toString() {
        return "StartEvent(" + this.getQName() + ")  [" + this.locator.getLineNumber() + "," + this.locator.getColumnNumber() + "]";
    }
}
