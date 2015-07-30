// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Option implements Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;
    public static final int UNINITIALIZED = -1;
    public static final int UNLIMITED_VALUES = -2;
    private String opt;
    private String longOpt;
    private String argName;
    private String description;
    private boolean required;
    private boolean optionalArg;
    private int numberOfArgs;
    private Object type;
    private List values;
    private char valuesep;
    
    public Option(final String opt, final String description) throws IllegalArgumentException {
        this(opt, null, false, description);
    }
    
    public Option(final String opt, final boolean hasArg, final String description) throws IllegalArgumentException {
        this(opt, null, hasArg, description);
    }
    
    public Option(final String opt, final String longOpt, final boolean hasArg, final String description) throws IllegalArgumentException {
        this.argName = "arg";
        this.numberOfArgs = -1;
        this.values = new ArrayList();
        OptionValidator.validateOption(opt);
        this.opt = opt;
        this.longOpt = longOpt;
        if (hasArg) {
            this.numberOfArgs = 1;
        }
        this.description = description;
    }
    
    public int getId() {
        return this.getKey().charAt(0);
    }
    
    String getKey() {
        if (this.opt == null) {
            return this.longOpt;
        }
        return this.opt;
    }
    
    public String getOpt() {
        return this.opt;
    }
    
    public Object getType() {
        return this.type;
    }
    
    public void setType(final Object type) {
        this.type = type;
    }
    
    public String getLongOpt() {
        return this.longOpt;
    }
    
    public void setLongOpt(final String longOpt) {
        this.longOpt = longOpt;
    }
    
    public void setOptionalArg(final boolean optionalArg) {
        this.optionalArg = optionalArg;
    }
    
    public boolean hasOptionalArg() {
        return this.optionalArg;
    }
    
    public boolean hasLongOpt() {
        return this.longOpt != null;
    }
    
    public boolean hasArg() {
        return this.numberOfArgs > 0 || this.numberOfArgs == -2;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public boolean isRequired() {
        return this.required;
    }
    
    public void setRequired(final boolean required) {
        this.required = required;
    }
    
    public void setArgName(final String argName) {
        this.argName = argName;
    }
    
    public String getArgName() {
        return this.argName;
    }
    
    public boolean hasArgName() {
        return this.argName != null && this.argName.length() > 0;
    }
    
    public boolean hasArgs() {
        return this.numberOfArgs > 1 || this.numberOfArgs == -2;
    }
    
    public void setArgs(final int num) {
        this.numberOfArgs = num;
    }
    
    public void setValueSeparator(final char sep) {
        this.valuesep = sep;
    }
    
    public char getValueSeparator() {
        return this.valuesep;
    }
    
    public boolean hasValueSeparator() {
        return this.valuesep > '\0';
    }
    
    public int getArgs() {
        return this.numberOfArgs;
    }
    
    void addValueForProcessing(final String value) {
        switch (this.numberOfArgs) {
            case -1: {
                throw new RuntimeException("NO_ARGS_ALLOWED");
            }
            default: {
                this.processValue(value);
            }
        }
    }
    
    private void processValue(String value) {
        if (this.hasValueSeparator()) {
            final char sep = this.getValueSeparator();
            for (int index = value.indexOf(sep); index != -1; index = value.indexOf(sep)) {
                if (this.values.size() == this.numberOfArgs - 1) {
                    break;
                }
                this.add(value.substring(0, index));
                value = value.substring(index + 1);
            }
        }
        this.add(value);
    }
    
    private void add(final String value) {
        if (this.numberOfArgs > 0 && this.values.size() > this.numberOfArgs - 1) {
            throw new RuntimeException("Cannot add value, list full.");
        }
        this.values.add(value);
    }
    
    public String getValue() {
        return this.hasNoValues() ? null : this.values.get(0);
    }
    
    public String getValue(final int index) throws IndexOutOfBoundsException {
        return this.hasNoValues() ? null : this.values.get(index);
    }
    
    public String getValue(final String defaultValue) {
        final String value = this.getValue();
        return (value != null) ? value : defaultValue;
    }
    
    public String[] getValues() {
        return (String[])(this.hasNoValues() ? null : ((String[])this.values.toArray(new String[this.values.size()])));
    }
    
    public List getValuesList() {
        return this.values;
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer().append("[ option: ");
        buf.append(this.opt);
        if (this.longOpt != null) {
            buf.append(" ").append(this.longOpt);
        }
        buf.append(" ");
        if (this.hasArgs()) {
            buf.append("[ARG...]");
        }
        else if (this.hasArg()) {
            buf.append(" [ARG]");
        }
        buf.append(" :: ").append(this.description);
        if (this.type != null) {
            buf.append(" :: ").append(this.type);
        }
        buf.append(" ]");
        return buf.toString();
    }
    
    private boolean hasNoValues() {
        return this.values.isEmpty();
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Option option = (Option)o;
        Label_0062: {
            if (this.opt != null) {
                if (this.opt.equals(option.opt)) {
                    break Label_0062;
                }
            }
            else if (option.opt == null) {
                break Label_0062;
            }
            return false;
        }
        if (this.longOpt != null) {
            if (this.longOpt.equals(option.longOpt)) {
                return true;
            }
        }
        else if (option.longOpt == null) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int result = (this.opt != null) ? this.opt.hashCode() : 0;
        result = 31 * result + ((this.longOpt != null) ? this.longOpt.hashCode() : 0);
        return result;
    }
    
    public Object clone() {
        try {
            final Option option = (Option)super.clone();
            option.values = new ArrayList(this.values);
            return option;
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException("A CloneNotSupportedException was thrown: " + cnse.getMessage());
        }
    }
    
    void clearValues() {
        this.values.clear();
    }
    
    public boolean addValue(final String value) {
        throw new UnsupportedOperationException("The addValue method is not intended for client use. Subclasses should use the addValueForProcessing method instead. ");
    }
}
