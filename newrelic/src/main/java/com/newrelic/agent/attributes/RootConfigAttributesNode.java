// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.attributes;

import java.util.Iterator;

public class RootConfigAttributesNode extends AttributesNode
{
    public RootConfigAttributesNode(final String dest) {
        super("", true, dest, true);
    }
    
    public Boolean applyRules(final String key) {
        Boolean result = null;
        for (final AttributesNode current : this.getChildren()) {
            result = current.applyRules(key);
            if (result != null) {
                break;
            }
        }
        return result;
    }
    
    public boolean addNode(final AttributesNode rule) {
        if (rule != null) {
            for (final AttributesNode current : this.getChildren()) {
                if (current.addNode(rule)) {
                    return true;
                }
            }
            this.addNodeToMe(rule);
            return true;
        }
        return false;
    }
}
