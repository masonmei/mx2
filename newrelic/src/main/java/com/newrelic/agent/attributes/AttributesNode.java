// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public class AttributesNode
{
    private static final String END_WILDCARD = "*";
    private final String original;
    private final String name;
    private final boolean hasEndWildcard;
    private final Set<AttributesNode> children;
    private AttributesNode parent;
    private boolean includeDestination;
    private final String destination;
    private final boolean isDefaultRule;
    
    public AttributesNode(final String pOriginal, final boolean isIncluded, final String dest, final boolean isDefault) {
        this.original = pOriginal;
        if (this.original.endsWith("*")) {
            this.name = this.original.substring(0, this.original.length() - 1);
            this.hasEndWildcard = true;
        }
        else {
            this.name = pOriginal;
            this.hasEndWildcard = false;
        }
        this.includeDestination = isIncluded;
        this.destination = dest;
        this.isDefaultRule = isDefault;
        this.children = new HashSet<AttributesNode>();
        this.parent = null;
    }
    
    protected Boolean applyRules(final String key) {
        Boolean result = null;
        if (this.matches(key)) {
            this.logMatch(key);
            result = this.includeDestination;
            for (final AttributesNode current : this.children) {
                final Boolean tmp = current.applyRules(key);
                if (tmp != null) {
                    result = tmp;
                    break;
                }
            }
        }
        return result;
    }
    
    private void logMatch(final String key) {
        if (Agent.LOG.isFinerEnabled()) {
            Agent.LOG.log(Level.FINEST, "{0}: Attribute key \"{1}\" matched {2} {3} rule \"{4}\"", new Object[] { this.destination, key, this.isDefaultRule ? "default" : "config", this.includeDestination ? "INCLUDE" : "EXCLUDE", this.original });
        }
    }
    
    public boolean addNode(final AttributesNode rule) {
        if (rule != null) {
            if (this.isSameString(rule)) {
                this.mergeIncludeExcludes(rule);
                return true;
            }
            if (this.isInputBefore(rule)) {
                this.addNodeBeforeMe(rule);
                return true;
            }
            if (this.isInputAfter(rule)) {
                for (final AttributesNode current : this.children) {
                    if (current.addNode(rule)) {
                        return true;
                    }
                }
                this.addNodeToMe(rule);
                return true;
            }
        }
        return false;
    }
    
    protected boolean matches(final String key) {
        return key != null && (this.hasEndWildcard ? key.startsWith(this.name) : this.name.equals(key));
    }
    
    protected boolean mightMatch(final String key) {
        return key != null && (key.startsWith(this.name) || this.name.startsWith(key));
    }
    
    protected boolean isIncludeDestination() {
        return this.includeDestination;
    }
    
    private boolean isSameString(final AttributesNode rule) {
        return this.original.equals(rule.original);
    }
    
    private boolean isInputBefore(final AttributesNode rule) {
        return rule.hasEndWildcard && this.name.startsWith(rule.name);
    }
    
    private boolean isInputAfter(final AttributesNode rule) {
        return this.hasEndWildcard && rule.name.startsWith(this.name);
    }
    
    private void addNodeBeforeMe(final AttributesNode rule) {
        final AttributesNode rulesParent = this.parent;
        this.moveChildrenToRuleAsNeeded(this.parent, rule);
        rulesParent.addNodeToMe(rule);
    }
    
    private void moveChildrenToRuleAsNeeded(final AttributesNode parent, final AttributesNode rule) {
        final Iterator<AttributesNode> it = parent.children.iterator();
        while (it.hasNext()) {
            final AttributesNode ar = it.next();
            if (ar.isInputBefore(rule)) {
                ar.parent = rule;
                it.remove();
                rule.children.add(ar);
            }
        }
    }
    
    protected void addNodeToMe(final AttributesNode rule) {
        rule.parent = this;
        this.children.add(rule);
    }
    
    protected boolean mergeIncludeExcludes(final AttributesNode rule) {
        return this.includeDestination = (this.includeDestination && rule.includeDestination);
    }
    
    public void printTrie() {
        final StringBuilder sb = new StringBuilder("Root: ").append(this.original).append("\n");
        final Queue<AttributesNode> q = new LinkedBlockingQueue<AttributesNode>();
        AttributesNode ar = this;
        while (ar != null) {
            sb.append("Parent: ");
            if (ar.parent != null) {
                sb.append(ar.parent.original);
            }
            else {
                sb.append("null");
            }
            sb.append(" This: ").append(ar.original).append(" Children: ");
            if (this.children != null) {
                for (final AttributesNode c : ar.children) {
                    sb.append(" ").append(c.original);
                    q.add(c);
                }
            }
            ar = q.poll();
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
    
    protected AttributesNode getParent() {
        return this.parent;
    }
    
    protected Set<AttributesNode> getChildren() {
        return this.children;
    }
}
