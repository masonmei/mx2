// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

public final class OptionBuilder
{
    private static String longopt;
    private static String description;
    private static String argName;
    private static boolean required;
    private static int numberOfArgs;
    private static Object type;
    private static boolean optionalArg;
    private static char valuesep;
    private static OptionBuilder instance;
    
    private static void reset() {
        OptionBuilder.description = null;
        OptionBuilder.argName = "arg";
        OptionBuilder.longopt = null;
        OptionBuilder.type = null;
        OptionBuilder.required = false;
        OptionBuilder.numberOfArgs = -1;
        OptionBuilder.optionalArg = false;
        OptionBuilder.valuesep = '\0';
    }
    
    public static OptionBuilder withLongOpt(final String newLongopt) {
        OptionBuilder.longopt = newLongopt;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder hasArg() {
        OptionBuilder.numberOfArgs = 1;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder hasArg(final boolean hasArg) {
        OptionBuilder.numberOfArgs = (hasArg ? 1 : -1);
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder withArgName(final String name) {
        OptionBuilder.argName = name;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder isRequired() {
        OptionBuilder.required = true;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder withValueSeparator(final char sep) {
        OptionBuilder.valuesep = sep;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder withValueSeparator() {
        OptionBuilder.valuesep = '=';
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder isRequired(final boolean newRequired) {
        OptionBuilder.required = newRequired;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder hasArgs() {
        OptionBuilder.numberOfArgs = -2;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder hasArgs(final int num) {
        OptionBuilder.numberOfArgs = num;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder hasOptionalArg() {
        OptionBuilder.numberOfArgs = 1;
        OptionBuilder.optionalArg = true;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder hasOptionalArgs() {
        OptionBuilder.numberOfArgs = -2;
        OptionBuilder.optionalArg = true;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder hasOptionalArgs(final int numArgs) {
        OptionBuilder.numberOfArgs = numArgs;
        OptionBuilder.optionalArg = true;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder withType(final Object newType) {
        OptionBuilder.type = newType;
        return OptionBuilder.instance;
    }
    
    public static OptionBuilder withDescription(final String newDescription) {
        OptionBuilder.description = newDescription;
        return OptionBuilder.instance;
    }
    
    public static Option create(final char opt) throws IllegalArgumentException {
        return create(String.valueOf(opt));
    }
    
    public static Option create() throws IllegalArgumentException {
        if (OptionBuilder.longopt == null) {
            reset();
            throw new IllegalArgumentException("must specify longopt");
        }
        return create(null);
    }
    
    public static Option create(final String opt) throws IllegalArgumentException {
        Option option = null;
        try {
            option = new Option(opt, OptionBuilder.description);
            option.setLongOpt(OptionBuilder.longopt);
            option.setRequired(OptionBuilder.required);
            option.setOptionalArg(OptionBuilder.optionalArg);
            option.setArgs(OptionBuilder.numberOfArgs);
            option.setType(OptionBuilder.type);
            option.setValueSeparator(OptionBuilder.valuesep);
            option.setArgName(OptionBuilder.argName);
        }
        finally {
            reset();
        }
        return option;
    }
    
    static {
        OptionBuilder.numberOfArgs = -1;
        OptionBuilder.instance = new OptionBuilder();
    }
}
