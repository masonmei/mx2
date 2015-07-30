// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

public class PatternOptionBuilder
{
    public static final Class STRING_VALUE;
    public static final Class OBJECT_VALUE;
    public static final Class NUMBER_VALUE;
    public static final Class DATE_VALUE;
    public static final Class CLASS_VALUE;
    public static final Class EXISTING_FILE_VALUE;
    public static final Class FILE_VALUE;
    public static final Class FILES_VALUE;
    public static final Class URL_VALUE;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$lang$Number;
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$java$lang$Class;
    static /* synthetic */ Class class$java$io$FileInputStream;
    static /* synthetic */ Class class$java$io$File;
    static /* synthetic */ Class array$Ljava$io$File;
    static /* synthetic */ Class class$java$net$URL;
    
    public static Object getValueClass(final char ch) {
        switch (ch) {
            case '@': {
                return PatternOptionBuilder.OBJECT_VALUE;
            }
            case ':': {
                return PatternOptionBuilder.STRING_VALUE;
            }
            case '%': {
                return PatternOptionBuilder.NUMBER_VALUE;
            }
            case '+': {
                return PatternOptionBuilder.CLASS_VALUE;
            }
            case '#': {
                return PatternOptionBuilder.DATE_VALUE;
            }
            case '<': {
                return PatternOptionBuilder.EXISTING_FILE_VALUE;
            }
            case '>': {
                return PatternOptionBuilder.FILE_VALUE;
            }
            case '*': {
                return PatternOptionBuilder.FILES_VALUE;
            }
            case '/': {
                return PatternOptionBuilder.URL_VALUE;
            }
            default: {
                return null;
            }
        }
    }
    
    public static boolean isValueCode(final char ch) {
        return ch == '@' || ch == ':' || ch == '%' || ch == '+' || ch == '#' || ch == '<' || ch == '>' || ch == '*' || ch == '/' || ch == '!';
    }
    
    public static Options parsePattern(final String pattern) {
        char opt = ' ';
        boolean required = false;
        Object type = null;
        final Options options = new Options();
        for (int i = 0; i < pattern.length(); ++i) {
            final char ch = pattern.charAt(i);
            if (!isValueCode(ch)) {
                if (opt != ' ') {
                    OptionBuilder.hasArg(type != null);
                    OptionBuilder.isRequired(required);
                    OptionBuilder.withType(type);
                    options.addOption(OptionBuilder.create(opt));
                    required = false;
                    type = null;
                    opt = ' ';
                }
                opt = ch;
            }
            else if (ch == '!') {
                required = true;
            }
            else {
                type = getValueClass(ch);
            }
        }
        if (opt != ' ') {
            OptionBuilder.hasArg(type != null);
            OptionBuilder.isRequired(required);
            OptionBuilder.withType(type);
            options.addOption(OptionBuilder.create(opt));
        }
        return options;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        STRING_VALUE = ((PatternOptionBuilder.class$java$lang$String == null) ? (PatternOptionBuilder.class$java$lang$String = class$("java.lang.String")) : PatternOptionBuilder.class$java$lang$String);
        OBJECT_VALUE = ((PatternOptionBuilder.class$java$lang$Object == null) ? (PatternOptionBuilder.class$java$lang$Object = class$("java.lang.Object")) : PatternOptionBuilder.class$java$lang$Object);
        NUMBER_VALUE = ((PatternOptionBuilder.class$java$lang$Number == null) ? (PatternOptionBuilder.class$java$lang$Number = class$("java.lang.Number")) : PatternOptionBuilder.class$java$lang$Number);
        DATE_VALUE = ((PatternOptionBuilder.class$java$util$Date == null) ? (PatternOptionBuilder.class$java$util$Date = class$("java.util.Date")) : PatternOptionBuilder.class$java$util$Date);
        CLASS_VALUE = ((PatternOptionBuilder.class$java$lang$Class == null) ? (PatternOptionBuilder.class$java$lang$Class = class$("java.lang.Class")) : PatternOptionBuilder.class$java$lang$Class);
        EXISTING_FILE_VALUE = ((PatternOptionBuilder.class$java$io$FileInputStream == null) ? (PatternOptionBuilder.class$java$io$FileInputStream = class$("java.io.FileInputStream")) : PatternOptionBuilder.class$java$io$FileInputStream);
        FILE_VALUE = ((PatternOptionBuilder.class$java$io$File == null) ? (PatternOptionBuilder.class$java$io$File = class$("java.io.File")) : PatternOptionBuilder.class$java$io$File);
        FILES_VALUE = ((PatternOptionBuilder.array$Ljava$io$File == null) ? (PatternOptionBuilder.array$Ljava$io$File = class$("[Ljava.io.File;")) : PatternOptionBuilder.array$Ljava$io$File);
        URL_VALUE = ((PatternOptionBuilder.class$java$net$URL == null) ? (PatternOptionBuilder.class$java$net$URL = class$("java.net.URL")) : PatternOptionBuilder.class$java$net$URL);
    }
}
