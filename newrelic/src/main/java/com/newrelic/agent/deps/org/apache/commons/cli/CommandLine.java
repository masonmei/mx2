// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;

public class CommandLine implements Serializable
{
    private static final long serialVersionUID = 1L;
    private List args;
    private List options;
    
    CommandLine() {
        this.args = new LinkedList();
        this.options = new ArrayList();
    }
    
    public boolean hasOption(final String opt) {
        return this.options.contains(this.resolveOption(opt));
    }
    
    public boolean hasOption(final char opt) {
        return this.hasOption(String.valueOf(opt));
    }
    
    public Object getOptionObject(final String opt) {
        try {
            return this.getParsedOptionValue(opt);
        }
        catch (ParseException pe) {
            System.err.println("Exception found converting " + opt + " to desired type: " + pe.getMessage());
            return null;
        }
    }
    
    public Object getParsedOptionValue(final String opt) throws ParseException {
        final String res = this.getOptionValue(opt);
        final Option option = this.resolveOption(opt);
        if (option == null) {
            return null;
        }
        final Object type = option.getType();
        return (res == null) ? null : TypeHandler.createValue(res, type);
    }
    
    public Object getOptionObject(final char opt) {
        return this.getOptionObject(String.valueOf(opt));
    }
    
    public String getOptionValue(final String opt) {
        final String[] values = this.getOptionValues(opt);
        return (values == null) ? null : values[0];
    }
    
    public String getOptionValue(final char opt) {
        return this.getOptionValue(String.valueOf(opt));
    }
    
    public String[] getOptionValues(final String opt) {
        final List values = new ArrayList();
        for (final Option option : this.options) {
            if (opt.equals(option.getOpt()) || opt.equals(option.getLongOpt())) {
                values.addAll(option.getValuesList());
            }
        }
        return (String[])(values.isEmpty() ? null : ((String[])values.toArray(new String[values.size()])));
    }
    
    private Option resolveOption(String opt) {
        opt = Util.stripLeadingHyphens(opt);
        for (final Option option : this.options) {
            if (opt.equals(option.getOpt())) {
                return option;
            }
            if (opt.equals(option.getLongOpt())) {
                return option;
            }
        }
        return null;
    }
    
    public String[] getOptionValues(final char opt) {
        return this.getOptionValues(String.valueOf(opt));
    }
    
    public String getOptionValue(final String opt, final String defaultValue) {
        final String answer = this.getOptionValue(opt);
        return (answer != null) ? answer : defaultValue;
    }
    
    public String getOptionValue(final char opt, final String defaultValue) {
        return this.getOptionValue(String.valueOf(opt), defaultValue);
    }
    
    public Properties getOptionProperties(final String opt) {
        final Properties props = new Properties();
        for (final Option option : this.options) {
            if (opt.equals(option.getOpt()) || opt.equals(option.getLongOpt())) {
                final List values = option.getValuesList();
                if (values.size() >= 2) {
                    props.put(values.get(0), values.get(1));
                }
                else {
                    if (values.size() != 1) {
                        continue;
                    }
                    ((Hashtable<Object, String>)props).put(values.get(0), "true");
                }
            }
        }
        return props;
    }
    
    public String[] getArgs() {
        final String[] answer = new String[this.args.size()];
        this.args.toArray(answer);
        return answer;
    }
    
    public List getArgList() {
        return this.args;
    }
    
    void addArg(final String arg) {
        this.args.add(arg);
    }
    
    void addOption(final Option opt) {
        this.options.add(opt);
    }
    
    public Iterator iterator() {
        return this.options.iterator();
    }
    
    public Option[] getOptions() {
        final Collection processed = this.options;
        final Option[] optionsArray = new Option[processed.size()];
        return processed.toArray(optionsArray);
    }
}
