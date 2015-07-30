// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import java.util.Hashtable;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import java.util.Iterator;
import java.util.Properties;
import org.xml.sax.Locator;
import java.util.HashMap;
import java.util.ArrayList;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.InPlayListener;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PropertyContainer;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class InterpretationContext extends ContextAwareBase implements PropertyContainer
{
    Stack<Object> objectStack;
    Map<String, Object> objectMap;
    Map<String, String> propertiesMap;
    Interpreter joranInterpreter;
    final List<InPlayListener> listenerList;
    DefaultNestedComponentRegistry defaultNestedComponentRegistry;
    
    public InterpretationContext(final Context context, final Interpreter joranInterpreter) {
        this.listenerList = new ArrayList<InPlayListener>();
        this.defaultNestedComponentRegistry = new DefaultNestedComponentRegistry();
        this.context = context;
        this.joranInterpreter = joranInterpreter;
        this.objectStack = new Stack<Object>();
        this.objectMap = new HashMap<String, Object>(5);
        this.propertiesMap = new HashMap<String, String>(5);
    }
    
    public DefaultNestedComponentRegistry getDefaultNestedComponentRegistry() {
        return this.defaultNestedComponentRegistry;
    }
    
    void setPropertiesMap(final Map<String, String> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }
    
    String updateLocationInfo(final String msg) {
        final Locator locator = this.joranInterpreter.getLocator();
        if (locator != null) {
            return msg + locator.getLineNumber() + ":" + locator.getColumnNumber();
        }
        return msg;
    }
    
    public Locator getLocator() {
        return this.joranInterpreter.getLocator();
    }
    
    public Interpreter getJoranInterpreter() {
        return this.joranInterpreter;
    }
    
    public Stack<Object> getObjectStack() {
        return this.objectStack;
    }
    
    public boolean isEmpty() {
        return this.objectStack.isEmpty();
    }
    
    public Object peekObject() {
        return this.objectStack.peek();
    }
    
    public void pushObject(final Object o) {
        this.objectStack.push(o);
    }
    
    public Object popObject() {
        return this.objectStack.pop();
    }
    
    public Object getObject(final int i) {
        return this.objectStack.get(i);
    }
    
    public Map<String, Object> getObjectMap() {
        return this.objectMap;
    }
    
    public void addSubstitutionProperty(final String key, String value) {
        if (key == null || value == null) {
            return;
        }
        value = value.trim();
        this.propertiesMap.put(key, value);
    }
    
    public void addSubstitutionProperties(final Properties props) {
        if (props == null) {
            return;
        }
        for (final String key : ((Hashtable<Object, V>)props).keySet()) {
            this.addSubstitutionProperty(key, props.getProperty(key));
        }
    }
    
    public String getProperty(final String key) {
        final String v = this.propertiesMap.get(key);
        if (v != null) {
            return v;
        }
        return this.context.getProperty(key);
    }
    
    public String subst(final String value) {
        if (value == null) {
            return null;
        }
        return OptionHelper.substVars(value, this, this.context);
    }
    
    public boolean isListenerListEmpty() {
        return this.listenerList.isEmpty();
    }
    
    public void addInPlayListener(final InPlayListener ipl) {
        if (this.listenerList.contains(ipl)) {
            this.addWarn("InPlayListener " + ipl + " has been already registered");
        }
        else {
            this.listenerList.add(ipl);
        }
    }
    
    public boolean removeInPlayListener(final InPlayListener ipl) {
        return this.listenerList.remove(ipl);
    }
    
    void fireInPlay(final SaxEvent event) {
        for (final InPlayListener ipl : this.listenerList) {
            ipl.inPlay(event);
        }
    }
}
