// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Properties;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public abstract class Parser implements CommandLineParser
{
    protected CommandLine cmd;
    private Options options;
    private List requiredOptions;
    
    protected void setOptions(final Options options) {
        this.options = options;
        this.requiredOptions = new ArrayList(options.getRequiredOptions());
    }
    
    protected Options getOptions() {
        return this.options;
    }
    
    protected List getRequiredOptions() {
        return this.requiredOptions;
    }
    
    protected abstract String[] flatten(final Options p0, final String[] p1, final boolean p2);
    
    public CommandLine parse(final Options options, final String[] arguments) throws ParseException {
        return this.parse(options, arguments, null, false);
    }
    
    public CommandLine parse(final Options options, final String[] arguments, final Properties properties) throws ParseException {
        return this.parse(options, arguments, properties, false);
    }
    
    public CommandLine parse(final Options options, final String[] arguments, final boolean stopAtNonOption) throws ParseException {
        return this.parse(options, arguments, null, stopAtNonOption);
    }
    
    public CommandLine parse(final Options options, String[] arguments, final Properties properties, final boolean stopAtNonOption) throws ParseException {
        for (final Option opt : options.helpOptions()) {
            opt.clearValues();
        }
        this.setOptions(options);
        this.cmd = new CommandLine();
        boolean eatTheRest = false;
        if (arguments == null) {
            arguments = new String[0];
        }
        final List tokenList = Arrays.asList(this.flatten(this.getOptions(), arguments, stopAtNonOption));
        final ListIterator iterator = tokenList.listIterator();
        while (iterator.hasNext()) {
            final String t = iterator.next();
            if ("--".equals(t)) {
                eatTheRest = true;
            }
            else if ("-".equals(t)) {
                if (stopAtNonOption) {
                    eatTheRest = true;
                }
                else {
                    this.cmd.addArg(t);
                }
            }
            else if (t.startsWith("-")) {
                if (stopAtNonOption && !this.getOptions().hasOption(t)) {
                    eatTheRest = true;
                    this.cmd.addArg(t);
                }
                else {
                    this.processOption(t, iterator);
                }
            }
            else {
                this.cmd.addArg(t);
                if (stopAtNonOption) {
                    eatTheRest = true;
                }
            }
            if (eatTheRest) {
                while (iterator.hasNext()) {
                    final String str = iterator.next();
                    if (!"--".equals(str)) {
                        this.cmd.addArg(str);
                    }
                }
            }
        }
        this.processProperties(properties);
        this.checkRequiredOptions();
        return this.cmd;
    }
    
    protected void processProperties(final Properties properties) {
        if (properties == null) {
            return;
        }
        final Enumeration e = properties.propertyNames();
        while (e.hasMoreElements()) {
            final String option = e.nextElement().toString();
            if (!this.cmd.hasOption(option)) {
                final Option opt = this.getOptions().getOption(option);
                final String value = properties.getProperty(option);
                Label_0130: {
                    if (opt.hasArg()) {
                        if (opt.getValues() != null) {
                            if (opt.getValues().length != 0) {
                                break Label_0130;
                            }
                        }
                        try {
                            opt.addValueForProcessing(value);
                        }
                        catch (RuntimeException exp) {}
                    }
                    else if (!"yes".equalsIgnoreCase(value) && !"true".equalsIgnoreCase(value) && !"1".equalsIgnoreCase(value)) {
                        break;
                    }
                }
                this.cmd.addOption(opt);
            }
        }
    }
    
    protected void checkRequiredOptions() throws MissingOptionException {
        if (!this.getRequiredOptions().isEmpty()) {
            throw new MissingOptionException(this.getRequiredOptions());
        }
    }
    
    public void processArgs(final Option opt, final ListIterator iter) throws ParseException {
        while (iter.hasNext()) {
            final String str = iter.next();
            if (this.getOptions().hasOption(str) && str.startsWith("-")) {
                iter.previous();
                break;
            }
            try {
                opt.addValueForProcessing(Util.stripLeadingAndTrailingQuotes(str));
            }
            catch (RuntimeException exp) {
                iter.previous();
                break;
            }
        }
        if (opt.getValues() == null && !opt.hasOptionalArg()) {
            throw new MissingArgumentException(opt);
        }
    }
    
    protected void processOption(final String arg, final ListIterator iter) throws ParseException {
        final boolean hasOption = this.getOptions().hasOption(arg);
        if (!hasOption) {
            throw new UnrecognizedOptionException("Unrecognized option: " + arg, arg);
        }
        final Option opt = (Option)this.getOptions().getOption(arg).clone();
        if (opt.isRequired()) {
            this.getRequiredOptions().remove(opt.getKey());
        }
        if (this.getOptions().getOptionGroup(opt) != null) {
            final OptionGroup group = this.getOptions().getOptionGroup(opt);
            if (group.isRequired()) {
                this.getRequiredOptions().remove(group);
            }
            group.setSelected(opt);
        }
        if (opt.hasArg()) {
            this.processArgs(opt, iter);
        }
        this.cmd.addOption(opt);
    }
}
