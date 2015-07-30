// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class Options implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Map shortOpts;
    private Map longOpts;
    private List requiredOpts;
    private Map optionGroups;
    
    public Options() {
        this.shortOpts = new HashMap();
        this.longOpts = new HashMap();
        this.requiredOpts = new ArrayList();
        this.optionGroups = new HashMap();
    }
    
    public Options addOptionGroup(final OptionGroup group) {
        final Iterator options = group.getOptions().iterator();
        if (group.isRequired()) {
            this.requiredOpts.add(group);
        }
        while (options.hasNext()) {
            final Option option = options.next();
            option.setRequired(false);
            this.addOption(option);
            this.optionGroups.put(option.getKey(), group);
        }
        return this;
    }
    
    Collection getOptionGroups() {
        return new HashSet(this.optionGroups.values());
    }
    
    public Options addOption(final String opt, final boolean hasArg, final String description) {
        this.addOption(opt, null, hasArg, description);
        return this;
    }
    
    public Options addOption(final String opt, final String longOpt, final boolean hasArg, final String description) {
        this.addOption(new Option(opt, longOpt, hasArg, description));
        return this;
    }
    
    public Options addOption(final Option opt) {
        final String key = opt.getKey();
        if (opt.hasLongOpt()) {
            this.longOpts.put(opt.getLongOpt(), opt);
        }
        if (opt.isRequired()) {
            if (this.requiredOpts.contains(key)) {
                this.requiredOpts.remove(this.requiredOpts.indexOf(key));
            }
            this.requiredOpts.add(key);
        }
        this.shortOpts.put(key, opt);
        return this;
    }
    
    public Collection getOptions() {
        return Collections.unmodifiableCollection((Collection<?>)this.helpOptions());
    }
    
    List helpOptions() {
        return new ArrayList(this.shortOpts.values());
    }
    
    public List getRequiredOptions() {
        return this.requiredOpts;
    }
    
    public Option getOption(String opt) {
        opt = Util.stripLeadingHyphens(opt);
        if (this.shortOpts.containsKey(opt)) {
            return this.shortOpts.get(opt);
        }
        return this.longOpts.get(opt);
    }
    
    public boolean hasOption(String opt) {
        opt = Util.stripLeadingHyphens(opt);
        return this.shortOpts.containsKey(opt) || this.longOpts.containsKey(opt);
    }
    
    public OptionGroup getOptionGroup(final Option opt) {
        return this.optionGroups.get(opt.getKey());
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("[ Options: [ short ");
        buf.append(this.shortOpts.toString());
        buf.append(" ] [ long ");
        buf.append(this.longOpts);
        buf.append(" ]");
        return buf.toString();
    }
}
