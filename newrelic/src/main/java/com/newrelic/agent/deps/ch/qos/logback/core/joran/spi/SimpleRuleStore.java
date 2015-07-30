// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import java.util.Iterator;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import java.util.ArrayList;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;
import java.util.List;
import java.util.HashMap;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class SimpleRuleStore extends ContextAwareBase implements RuleStore
{
    static String ANY;
    HashMap<Pattern, List<Action>> rules;
    
    public SimpleRuleStore(final Context context) {
        this.rules = new HashMap<Pattern, List<Action>>();
        this.setContext(context);
    }
    
    public void addRule(final Pattern pattern, final Action action) {
        action.setContext(this.context);
        List<Action> a4p = this.rules.get(pattern);
        if (a4p == null) {
            a4p = new ArrayList<Action>();
            this.rules.put(pattern, a4p);
        }
        a4p.add(action);
    }
    
    public void addRule(final Pattern pattern, final String actionClassName) {
        Action action = null;
        try {
            action = (Action)OptionHelper.instantiateByClassName(actionClassName, Action.class, this.context);
        }
        catch (Exception e) {
            this.addError("Could not instantiate class [" + actionClassName + "]", e);
        }
        if (action != null) {
            this.addRule(pattern, action);
        }
    }
    
    public List matchActions(final Pattern currentPattern) {
        List actionList;
        if ((actionList = this.rules.get(currentPattern)) != null) {
            return actionList;
        }
        if ((actionList = this.tailMatch(currentPattern)) != null) {
            return actionList;
        }
        if ((actionList = this.prefixMatch(currentPattern)) != null) {
            return actionList;
        }
        if ((actionList = this.middleMatch(currentPattern)) != null) {
            return actionList;
        }
        return null;
    }
    
    List tailMatch(final Pattern currentPattern) {
        int max = 0;
        Pattern longestMatchingPattern = null;
        for (final Pattern p : this.rules.keySet()) {
            if (p.size() > 1 && p.get(0).equals(SimpleRuleStore.ANY)) {
                final int r = currentPattern.getTailMatchLength(p);
                if (r <= max) {
                    continue;
                }
                max = r;
                longestMatchingPattern = p;
            }
        }
        if (longestMatchingPattern != null) {
            return this.rules.get(longestMatchingPattern);
        }
        return null;
    }
    
    List prefixMatch(final Pattern currentPattern) {
        int max = 0;
        Pattern longestMatchingPattern = null;
        for (final Pattern p : this.rules.keySet()) {
            final String last = p.peekLast();
            if (SimpleRuleStore.ANY.equals(last)) {
                final int r = currentPattern.getPrefixMatchLength(p);
                if (r != p.size() - 1 || r <= max) {
                    continue;
                }
                max = r;
                longestMatchingPattern = p;
            }
        }
        if (longestMatchingPattern != null) {
            return this.rules.get(longestMatchingPattern);
        }
        return null;
    }
    
    List middleMatch(final Pattern currentPattern) {
        int max = 0;
        Pattern longestMatchingPattern = null;
        for (final Pattern p : this.rules.keySet()) {
            final String last = p.peekLast();
            String first = null;
            if (p.size() > 1) {
                first = p.get(0);
            }
            if (SimpleRuleStore.ANY.equals(last) && SimpleRuleStore.ANY.equals(first)) {
                final List<String> partList = p.getCopyOfPartList();
                if (partList.size() > 2) {
                    partList.remove(0);
                    partList.remove(partList.size() - 1);
                }
                int r = 0;
                final Pattern clone = new Pattern(partList);
                if (currentPattern.isContained(clone)) {
                    r = clone.size();
                }
                if (r <= max) {
                    continue;
                }
                max = r;
                longestMatchingPattern = p;
            }
        }
        if (longestMatchingPattern != null) {
            return this.rules.get(longestMatchingPattern);
        }
        return null;
    }
    
    public String toString() {
        final String TAB = "  ";
        final StringBuilder retValue = new StringBuilder();
        retValue.append("SimpleRuleStore ( ").append("rules = ").append(this.rules).append("  ").append(" )");
        return retValue.toString();
    }
    
    static {
        SimpleRuleStore.ANY = "*";
    }
}
