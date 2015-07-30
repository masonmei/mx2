// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

public class AlreadySelectedException extends ParseException
{
    private OptionGroup group;
    private Option option;
    
    public AlreadySelectedException(final String message) {
        super(message);
    }
    
    public AlreadySelectedException(final OptionGroup group, final Option option) {
        this("The option '" + option.getKey() + "' was specified but an option from this group " + "has already been selected: '" + group.getSelected() + "'");
        this.group = group;
        this.option = option;
    }
    
    public OptionGroup getOptionGroup() {
        return this.group;
    }
    
    public Option getOption() {
        return this.option;
    }
}
