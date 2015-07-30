// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class OptionGroup implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Map optionMap;
    private String selected;
    private boolean required;
    
    public OptionGroup() {
        this.optionMap = new HashMap();
    }
    
    public OptionGroup addOption(final Option option) {
        this.optionMap.put(option.getKey(), option);
        return this;
    }
    
    public Collection getNames() {
        return this.optionMap.keySet();
    }
    
    public Collection getOptions() {
        return this.optionMap.values();
    }
    
    public void setSelected(final Option option) throws AlreadySelectedException {
        if (this.selected == null || this.selected.equals(option.getOpt())) {
            this.selected = option.getOpt();
            return;
        }
        throw new AlreadySelectedException(this, option);
    }
    
    public String getSelected() {
        return this.selected;
    }
    
    public void setRequired(final boolean required) {
        this.required = required;
    }
    
    public boolean isRequired() {
        return this.required;
    }
    
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        final Iterator iter = this.getOptions().iterator();
        buff.append("[");
        while (iter.hasNext()) {
            final Option option = iter.next();
            if (option.getOpt() != null) {
                buff.append("-");
                buff.append(option.getOpt());
            }
            else {
                buff.append("--");
                buff.append(option.getLongOpt());
            }
            buff.append(" ");
            buff.append(option.getDescription());
            if (iter.hasNext()) {
                buff.append(", ");
            }
        }
        buff.append("]");
        return buff.toString();
    }
}
