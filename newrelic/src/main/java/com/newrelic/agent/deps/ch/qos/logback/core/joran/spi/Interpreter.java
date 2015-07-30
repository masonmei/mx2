// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import java.util.Vector;
import java.util.Iterator;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.EndEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.BodyEvent;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.StartEvent;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.util.Stack;
import org.xml.sax.Locator;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.ImplicitAction;
import java.util.ArrayList;
import java.util.List;

public class Interpreter
{
    private static List EMPTY_LIST;
    private final RuleStore ruleStore;
    private final InterpretationContext interpretationContext;
    private final ArrayList<ImplicitAction> implicitActions;
    private final CAI_WithLocatorSupport cai;
    private Pattern pattern;
    Locator locator;
    EventPlayer eventPlayer;
    Stack<List> actionListStack;
    Pattern skip;
    
    public Interpreter(final Context context, final RuleStore rs, final Pattern initialPattern) {
        this.skip = null;
        (this.cai = new CAI_WithLocatorSupport(this)).setContext(context);
        this.ruleStore = rs;
        this.interpretationContext = new InterpretationContext(context, this);
        this.implicitActions = new ArrayList<ImplicitAction>(3);
        this.pattern = initialPattern;
        this.actionListStack = new Stack<List>();
        this.eventPlayer = new EventPlayer(this);
    }
    
    public EventPlayer getEventPlayer() {
        return this.eventPlayer;
    }
    
    public void setInterpretationContextPropertiesMap(final Map<String, String> propertiesMap) {
        this.interpretationContext.setPropertiesMap(propertiesMap);
    }
    
    public InterpretationContext getExecutionContext() {
        return this.getInterpretationContext();
    }
    
    public InterpretationContext getInterpretationContext() {
        return this.interpretationContext;
    }
    
    public void startDocument() {
    }
    
    public void startElement(final StartEvent se) {
        this.setDocumentLocator(se.getLocator());
        this.startElement(se.namespaceURI, se.localName, se.qName, se.attributes);
    }
    
    private void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) {
        final String tagName = this.getTagName(localName, qName);
        this.pattern.push(tagName);
        if (this.skip != null) {
            this.pushEmptyActionList();
            return;
        }
        final List applicableActionList = this.getApplicableActionList(this.pattern, atts);
        if (applicableActionList != null) {
            this.actionListStack.add(applicableActionList);
            this.callBeginAction(applicableActionList, tagName, atts);
        }
        else {
            this.pushEmptyActionList();
            final String errMsg = "no applicable action for [" + tagName + "], current pattern is [" + this.pattern + "]";
            this.cai.addError(errMsg);
        }
    }
    
    private void pushEmptyActionList() {
        this.actionListStack.add(Interpreter.EMPTY_LIST);
    }
    
    public void characters(final BodyEvent be) {
        this.setDocumentLocator(be.locator);
        String body = be.getText();
        final List applicableActionList = this.actionListStack.peek();
        if (body != null) {
            body = body.trim();
            if (body.length() > 0) {
                this.callBodyAction(applicableActionList, body);
            }
        }
    }
    
    public void endElement(final EndEvent endEvent) {
        this.setDocumentLocator(endEvent.locator);
        this.endElement(endEvent.namespaceURI, endEvent.localName, endEvent.qName);
    }
    
    private void endElement(final String namespaceURI, final String localName, final String qName) {
        final List applicableActionList = this.actionListStack.pop();
        if (this.skip != null) {
            if (this.skip.equals(this.pattern)) {
                this.skip = null;
            }
        }
        else if (applicableActionList != Interpreter.EMPTY_LIST) {
            this.callEndAction(applicableActionList, this.getTagName(localName, qName));
        }
        this.pattern.pop();
    }
    
    public Locator getLocator() {
        return this.locator;
    }
    
    public void setDocumentLocator(final Locator l) {
        this.locator = l;
    }
    
    String getTagName(final String localName, final String qName) {
        String tagName = localName;
        if (tagName == null || tagName.length() < 1) {
            tagName = qName;
        }
        return tagName;
    }
    
    public void addImplicitAction(final ImplicitAction ia) {
        this.implicitActions.add(ia);
    }
    
    List lookupImplicitAction(final Pattern pattern, final Attributes attributes, final InterpretationContext ec) {
        for (int len = this.implicitActions.size(), i = 0; i < len; ++i) {
            final ImplicitAction ia = this.implicitActions.get(i);
            if (ia.isApplicable(pattern, attributes, ec)) {
                final List<Action> actionList = new ArrayList<Action>(1);
                actionList.add(ia);
                return actionList;
            }
        }
        return null;
    }
    
    List getApplicableActionList(final Pattern pattern, final Attributes attributes) {
        List applicableActionList = this.ruleStore.matchActions(pattern);
        if (applicableActionList == null) {
            applicableActionList = this.lookupImplicitAction(pattern, attributes, this.interpretationContext);
        }
        return applicableActionList;
    }
    
    void callBeginAction(final List applicableActionList, final String tagName, final Attributes atts) {
        if (applicableActionList == null) {
            return;
        }
        for (final Action action : applicableActionList) {
            try {
                action.begin(this.interpretationContext, tagName, atts);
            }
            catch (ActionException e) {
                this.skip = (Pattern)this.pattern.clone();
                this.cai.addError("ActionException in Action for tag [" + tagName + "]", e);
            }
            catch (RuntimeException e2) {
                this.skip = (Pattern)this.pattern.clone();
                this.cai.addError("RuntimeException in Action for tag [" + tagName + "]", e2);
            }
        }
    }
    
    private void callBodyAction(final List applicableActionList, final String body) {
        if (applicableActionList == null) {
            return;
        }
        for (final Action action : applicableActionList) {
            try {
                action.body(this.interpretationContext, body);
            }
            catch (ActionException ae) {
                this.cai.addError("Exception in end() methd for action [" + action + "]", ae);
            }
        }
    }
    
    private void callEndAction(final List applicableActionList, final String tagName) {
        if (applicableActionList == null) {
            return;
        }
        for (final Action action : applicableActionList) {
            try {
                action.end(this.interpretationContext, tagName);
            }
            catch (ActionException ae) {
                this.cai.addError("ActionException in Action for tag [" + tagName + "]", ae);
            }
            catch (RuntimeException e) {
                this.cai.addError("RuntimeException in Action for tag [" + tagName + "]", e);
            }
        }
    }
    
    public RuleStore getRuleStore() {
        return this.ruleStore;
    }
    
    static {
        Interpreter.EMPTY_LIST = new Vector(0);
    }
}
